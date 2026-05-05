package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuneLootModifier extends LootModifier {

    public record WeightedTag(Optional<TagKey<Rune>> tag, int weight) {
        public static final Codec<WeightedTag> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(wt -> wt.tag().map(TagKey::location)),
                Codec.INT.fieldOf("weight").forGetter(WeightedTag::weight)
        ).apply(inst, (id, w) -> new WeightedTag(id.map(rl -> TagKey.create(RERunes.REGISTRY_KEY, rl)), w)));
    }

    public static final MapCodec<RuneLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst)
                    .and(WeightedTag.CODEC.listOf().optionalFieldOf("rune_tags", List.of()).forGetter(m -> m.runeTags))
                    .apply(inst, RuneLootModifier::new));

    private final List<WeightedTag> runeTags;

    public RuneLootModifier(LootItemCondition[] conditions, List<WeightedTag> runeTags) {
        super(conditions);
        this.runeTags = runeTags;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        var runeRegistry = context.getLevel().registryAccess().registryOrThrow(RERunes.REGISTRY_KEY);
        List<? extends Holder<Rune>> allRunes = runeRegistry.holders().toList();

        List<ItemStack> toAdd = new ArrayList<>();
        List<ItemStack> toRemove = new ArrayList<>();

        for (ItemStack stack : generatedLoot) {
            if (stack.has(REDataComponents.RUNES.get()))
                continue;

            var enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null && !enchantments.isEmpty()) {
                if (RuneFeature.disableExperience)
                    stack.remove(DataComponents.ENCHANTMENTS);
                List<? extends Holder<Rune>> pool = resolvePool(stack, runeRegistry, allRunes, context.getRandom(), false);
                RuneHelper.addRandomRunes(stack, enchantments.size(), context.getRandom(), pool);
            } else if (stack.is(Items.ENCHANTED_BOOK)) {
                var stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
                if (stored != null && !stored.isEmpty()) {
                    List<? extends Holder<Rune>> pool = resolvePool(stack, runeRegistry, allRunes, context.getRandom(), true);
                    ItemStack runeItem = RuneHelper.createRandomRuneItem(context.getRandom(), pool);
                    if (!runeItem.isEmpty())
                        toAdd.add(runeItem);
                    if (RuneFeature.disableExperience)
                        toRemove.add(stack);
                }
            }
        }

        generatedLoot.addAll(toAdd);
        toRemove.forEach(generatedLoot::remove);
        return generatedLoot;
    }

    private List<? extends Holder<Rune>> resolvePool(ItemStack stack, Registry<Rune> runeRegistry, List<? extends Holder<Rune>> allRunes, RandomSource random, boolean isBook) {
        if (runeTags.isEmpty()) return allRunes;

        List<WeightedTag> eligible = runeTags.stream()
                .filter(wt -> {
                    if (wt.tag().isEmpty()) return true;
                    return runeRegistry.getTag(wt.tag().get())
                            .stream()
                            .flatMap(HolderSet.ListBacked::stream)
                            .anyMatch(h -> {
                                var rune = h.value();
                                if (!rune.isEnabled()) return false;
                                if (isBook) return true;
                                return stack.is(TagKey.create(Registries.ITEM, rune.getApplicableToItemTag()));
                            });
                })
                .toList();

        if (eligible.isEmpty()) return allRunes;

        int totalWeight = eligible.stream().mapToInt(WeightedTag::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedTag wt : eligible) {
            cumulative += wt.weight();
            if (roll < cumulative) {
                if (wt.tag().isEmpty()) return allRunes;
                return runeRegistry.getTag(wt.tag().get())
                        .stream()
                        .flatMap(HolderSet.ListBacked::stream)
                        .toList();
            }
        }
        return allRunes;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
