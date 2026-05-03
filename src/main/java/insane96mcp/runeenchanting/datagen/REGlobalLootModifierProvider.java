package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.loot.RuneCatchAllLootModifier;
import insane96mcp.runeenchanting.loot.RuneLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class REGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public REGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, RuneEnchanting.MOD_ID);
    }

    @Override
    protected void start() {
        addRuneLootModifier("abandoned_mineshaft", RERuneTagProvider.LOOT_ABANDONED_MINESHAFT);
        addRuneLootModifier("igloo_chest", RERuneTagProvider.LOOT_IGLOO_CHEST);

        add("catch_all", new RuneCatchAllLootModifier(new LootItemCondition[0]));
    }

    private static LootItemCondition[] conditions(ResourceLocation lootTable) {
        return new LootItemCondition[]{LootTableIdCondition.builder(lootTable).build()};
    }

    public void addRuneLootModifier(String lootTable, TagKey<Rune> runesTag) {
        add(lootTable, new RuneLootModifier(conditions(ResourceLocation.parse("minecraft:chests/" + lootTable)), runesTag.location()));
    }
}
