package insane96mcp.runeenchanting.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

public class RuneLootModifier extends LootModifier {
    public static final MapCodec<RuneLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst)
                    .and(ResourceLocation.CODEC.fieldOf("rune_tag").forGetter(m -> m.runeTag.location()))
                    .apply(inst, RuneLootModifier::new));

    private final TagKey<Rune> runeTag;

    public RuneLootModifier(LootItemCondition[] conditions, ResourceLocation runeTagId) {
        super(conditions);
        this.runeTag = TagKey.create(RERunes.REGISTRY_KEY, runeTagId);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        var runeRegistry = context.getLevel().registryAccess().registryOrThrow(RERunes.REGISTRY_KEY);
        List<Holder<Rune>> tagRunes = runeRegistry.getTag(runeTag)
                .stream()
                .flatMap(HolderSet.ListBacked::stream)
                .toList();

        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.has(REDataComponents.RUNES.get()))
                continue;

            var enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null && !enchantments.isEmpty()) {
                if (RuneFeature.disableExperience)
                    stack.remove(DataComponents.ENCHANTMENTS);
                RuneHelper.addRandomRunes(stack, enchantments.size(), context.getRandom(), tagRunes);
            } else if (stack.is(Items.ENCHANTED_BOOK)) {
                var stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
                if (stored != null && !stored.isEmpty()) {
                    ItemStack runeItem = RuneHelper.createRandomRuneItem(context.getRandom(), tagRunes);
                    if (!runeItem.isEmpty())
                        generatedLoot.add(runeItem);
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
