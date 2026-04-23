package insane96mcp.gemenhancing;

import insane96mcp.insanelib.setup.ILModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(GemEnhancing.MOD_ID)
public class GemEnhancing {
    public static final String MOD_ID = "gemenhancing";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ILModConfig CONFIG;
    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public GemEnhancing(IEventBus modEventBus, ModContainer modContainer) {
        CONFIG = new ILModConfig(location("main"), "Single Module", ModConfig.Type.COMMON, modEventBus, GemEnhancing.class.getClassLoader());
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.spec, MOD_ID + "/common.toml");
        modEventBus.addListener(this::commonSetup);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String lang(String path) {
        return MOD_ID + "." + path;
    }

    public static MutableComponent translatableLang(String path) {
        return Component.translatable(lang(path));
    }


    private void commonSetup(FMLCommonSetupEvent event) {

    }
}
