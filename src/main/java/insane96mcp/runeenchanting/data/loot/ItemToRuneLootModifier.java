package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

public class ItemToRuneLootModifier extends LootModifier {
    public final Item item;
    public final float chance;

    public static final MapCodec<ItemToRuneLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst)
                    .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                    .apply(inst, ItemToRuneLootModifier::new));

    public ItemToRuneLootModifier(LootItemCondition[] conditions, Item item, float chance) {
        super(conditions);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        List<? extends Holder<Rune>> allRunes = context.getLevel().registryAccess()
                .registryOrThrow(RERunes.REGISTRY_KEY)
                .holders()
                .toList();

        for (int i = generatedLoot.size() - 1; i >= 0; i--) {
            ItemStack stack = generatedLoot.get(i);
            if (!stack.is(this.item))
                continue;
            generatedLoot.remove(i);
            int runeCount = MathHelper.getAmountWithDecimalChance(context.getRandom(), this.chance * stack.getCount());
            for (int j = 0; j < runeCount; j++) {
                ItemStack runeItem = RuneHelper.createRandomRuneItem(context.getRandom(), allRunes);
                if (!runeItem.isEmpty())
                    generatedLoot.add(runeItem);
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
