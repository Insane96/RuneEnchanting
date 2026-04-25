package insane96mcp.runeenchanting;

import com.mojang.logging.LogUtils;
import insane96mcp.insanelib.setup.ILModConfig;
import insane96mcp.runeenchanting.datagen.REItemTagProvider;
import insane96mcp.runeenchanting.setup.REItemComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(RuneEnchanting.MOD_ID)
public class RuneEnchanting {
    public static final String MOD_ID = "runeenchanting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ILModConfig CONFIG;
    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public RuneEnchanting(IEventBus modEventBus, ModContainer modContainer) {
        CONFIG = new ILModConfig(location("main"), "Single Module", ModConfig.Type.COMMON, modEventBus, RuneEnchanting.class.getClassLoader());
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.spec);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(RERunes::registerRegistry);
        modEventBus.addListener(RuneEnchanting::gatherData);
        RERunes.RUNES.register(modEventBus);
        RERunes.registerConfigs(modEventBus, modContainer);
        REItemComponents.REGISTRY.register(modEventBus);
        REItems.REGISTRY.register(modEventBus);
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

    public static void gatherData(GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(output, lookupProvider, MOD_ID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {}
        };
        event.getGenerator().addProvider(event.includeServer(), blockTagsProvider);
        event.getGenerator().addProvider(event.includeServer(),
                new REItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), MOD_ID, existingFileHelper));
    }
}
