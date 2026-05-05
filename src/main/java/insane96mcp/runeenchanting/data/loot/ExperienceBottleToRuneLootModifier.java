package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

public class ExperienceBottleToRuneLootModifier extends LootModifier {
    public static final float CHANCE = 0.4f;

    public static final MapCodec<ExperienceBottleToRuneLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, ExperienceBottleToRuneLootModifier::new));

    public ExperienceBottleToRuneLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        List<? extends Holder<Rune>> allRunes = context.getLevel().registryAccess()
                .registryOrThrow(RERunes.REGISTRY_KEY)
                .holders()
                .toList();

        for (int i = generatedLoot.size() - 1; i >= 0; i--) {
            ItemStack stack = generatedLoot.get(i);
            if (!stack.is(Items.EXPERIENCE_BOTTLE))
                continue;
            generatedLoot.remove(i);
            if (MathHelper.getAmountWithDecimalChance(context.getRandom(), CHANCE) > 0) {
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
