package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.setup.REBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class REBlockLootProvider extends BlockLootSubProvider {
    public REBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        //The stored runes are dropped by RunicTableBlock#onRemove, not by the loot table
        dropSelf(REBlocks.RUNIC_TABLE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return REBlocks.REGISTRY.getEntries().stream().<Block>map(holder -> holder.value()).toList();
    }
}
