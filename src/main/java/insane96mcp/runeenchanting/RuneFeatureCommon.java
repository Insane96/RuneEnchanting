package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.ConfigType;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.Module;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.IntegratedPack;
import net.neoforged.fml.config.ModConfig;

/**
 * COMMON as the integrated data pack is picked when the world's data packs are loaded, before SERVER configs are
 */
@LoadFeature(module = RuneEnchanting.MAIN_MODULE, canBeDisabled = false)
@ConfigType(ModConfig.Type.COMMON)
public class RuneFeatureCommon extends Feature {
    @Config(description = """
            A data pack will be enabled that changes the following:
            * Removes the enchanting table recipe
            * Changes Channeling enchantment (and subsequently the Channeling rune) to work in rain too""")
    public static Boolean integratedDataPack = true;

    @Override
    public void init(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super.init(module, enabledByDefault, canBeDisabled);
        IntegratedPack.addServerPack(RuneEnchanting.MOD_ID, "enchantment_changes", "Rune Enchanting vanilla changes", () -> integratedDataPack);
    }
}
