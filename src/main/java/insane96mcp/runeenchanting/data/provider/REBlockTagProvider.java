package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class REBlockTagProvider extends BlockTagsProvider {
    /**
     * Blocks that runes breaking extra blocks (Veining, Tunneling) can break with shears, even if shears aren't the correct tool for them nor mine them faster (e.g. grass and other blocks without hardness)
     */
    public static final TagKey<Block> EXTRA_BREAKABLE_WITH_SHEARS = TagKey.create(Registries.BLOCK, RuneEnchanting.id("extra_breakable_with_shears"));

    public REBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(REBlocks.RUNIC_TABLE.get());

        tag(EXTRA_BREAKABLE_WITH_SHEARS).add(
                Blocks.SHORT_GRASS, Blocks.FERN, Blocks.TALL_GRASS, Blocks.LARGE_FERN,
                Blocks.DEAD_BUSH, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.HANGING_ROOTS,
                Blocks.NETHER_SPROUTS,
                Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT,
                Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
    }
}
