package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.loot.ExperienceBottleToRuneLootModifier;
import insane96mcp.runeenchanting.data.loot.RuneCatchAllLootModifier;
import insane96mcp.runeenchanting.data.loot.RuneLootModifier;
import insane96mcp.runeenchanting.data.loot.VoidCurseLootModifier;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class REGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public REGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, RuneEnchanting.MOD_ID);
    }

    @Override
    protected void start() {
        addRuneLootModifier("abandoned_mineshaft", RERuneTagProvider.LOOT_ABANDONED_MINESHAFT);
        addRuneLootModifier("ancient_city", List.of(
                new RuneLootModifier.WeightedTag(Optional.of(RERuneTagProvider.LOOT_ANCIENT_CITY_SPECIFIC), 3),
                new RuneLootModifier.WeightedTag(Optional.of(RERuneTagProvider.LOOT_ANCIENT_CITY), 1)
        ));
        addRuneLootModifier("bastion_other", List.of(
                new RuneLootModifier.WeightedTag(Optional.of(RERuneTagProvider.LOOT_BASTION_OTHER_SPECIFIC), 3),
                new RuneLootModifier.WeightedTag(Optional.empty(), 1)
        ));
        addRuneLootModifier("trial_chambers/reward_ominous_rare", List.of(
                new RuneLootModifier.WeightedTag(Optional.of(RERuneTagProvider.LOOT_TRIAL_CHAMBERS_REWARD_OMINOUS_RARE), 3),
                new RuneLootModifier.WeightedTag(Optional.empty(), 1)
        ));
        addRuneLootModifier("igloo_chest", RERuneTagProvider.LOOT_IGLOO_CHEST);

        add("void_curse", new VoidCurseLootModifier(new LootItemCondition[0]));
        add("experience_bottle_to_rune", new ExperienceBottleToRuneLootModifier(new LootItemCondition[0]));
        add("catch_all", new RuneCatchAllLootModifier(new LootItemCondition[0]));
    }

    private static LootItemCondition[] conditions(ResourceLocation lootTable) {
        return new LootItemCondition[]{LootTableIdCondition.builder(lootTable).build()};
    }

    public void addRuneLootModifier(String lootTable, List<RuneLootModifier.WeightedTag> weightedTags) {
        add(lootTable, new RuneLootModifier(conditions(ResourceLocation.parse("minecraft:chests/" + lootTable)), weightedTags));
    }

    public void addRuneLootModifier(String lootTable, TagKey<Rune> runesTag) {
        add(lootTable, new RuneLootModifier(
                conditions(ResourceLocation.parse("minecraft:chests/" + lootTable)),
                List.of(new RuneLootModifier.WeightedTag(Optional.of(runesTag), 1))
        ));
    }
}
