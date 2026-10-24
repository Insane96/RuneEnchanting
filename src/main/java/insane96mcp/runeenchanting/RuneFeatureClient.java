package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.config.Config;

@LoadFeature(module = RuneEnchanting.CLIENT_MODULE, name = "Rune", canBeDisabled = false)
public class RuneFeatureClient extends Feature {
    @Config(description = "If true, enchanting related items are hidden from creative inventory")
    public static Boolean hideEnchantingRelatedItems = true;

    @Config(description = "If true, infos that are usually shown only with shift held down, will always show instead")
    public static Boolean alwaysShowExtraInfos = false;

    @Config(description = "If true, curse names in tooltips randomly glitch (characters get obfuscated every now and then)")
    public static Boolean glitchCurseNames = true;
}
