package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.block.RunicTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(RuneEnchanting.MOD_ID);

    public static final DeferredBlock<RunicTableBlock> RUNIC_TABLE = REGISTRY.registerBlock("runic_table", RunicTableBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .strength(5.0F, 1200.0F));
}
