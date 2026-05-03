package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.loot.ExperienceBottleToRuneLootModifier;
import insane96mcp.runeenchanting.loot.RuneCatchAllLootModifier;
import insane96mcp.runeenchanting.loot.RuneLootModifier;
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
        addRuneLootModifier("bastion_bridge");
        addRuneLootModifier("igloo_chest", RERuneTagProvider.LOOT_IGLOO_CHEST);

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

    public void addRuneLootModifier(String lootTable) {
        add(lootTable, new RuneLootModifier(conditions(ResourceLocation.parse("minecraft:chests/" + lootTable)), List.of()));
    }
}
