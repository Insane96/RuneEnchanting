package insane96mcp.runeenchanting;

import com.mojang.logging.LogUtils;
import insane96mcp.insanelib.setup.ILModConfig;
import insane96mcp.runeenchanting.data.EnchantmentToRuneReloadListener;
import insane96mcp.runeenchanting.datagen.REEnchantmentTagProvider;
import insane96mcp.runeenchanting.datagen.REEntityTypeTagProvider;
import insane96mcp.runeenchanting.datagen.REItemTagProvider;
import insane96mcp.runeenchanting.datagen.RELanguageProvider;
import insane96mcp.runeenchanting.network.NetworkHandler;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(RuneEnchanting.MOD_ID)
public class RuneEnchanting {
    public static final String MOD_ID = "runeenchanting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ILModConfig CONFIG;
    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public RuneEnchanting(IEventBus modEventBus, ModContainer modContainer) {
        CONFIG = new ILModConfig(id("main"), "Single Module", ModConfig.Type.COMMON, modEventBus, RuneEnchanting.class.getClassLoader());
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.spec, MOD_ID + "/main.toml");
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(RERunes::registerRegistry);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        modEventBus.addListener(RuneEnchanting::gatherData);
        modEventBus.addListener(NetworkHandler::register);
        RERunes.RUNES.register(modEventBus);
        RERunes.registerConfigs(modEventBus, modContainer);
        REDataComponents.REGISTRY.register(modEventBus);
        REItems.REGISTRY.register(modEventBus);
    }

    public static ResourceLocation id(String path) {
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

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(EnchantmentToRuneReloadListener.INSTANCE);
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
        event.getGenerator().addProvider(event.includeServer(),
                new REEntityTypeTagProvider(output, lookupProvider, MOD_ID, existingFileHelper));
        event.getGenerator().addProvider(event.includeServer(),
                new REEnchantmentTagProvider(output, lookupProvider, MOD_ID, existingFileHelper));
        event.getGenerator().addProvider(event.includeClient(),
                new RELanguageProvider(output));
    }

    public static EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
        final EquipmentSlot slot = stack.getEquipmentSlot();
        if (slot != null) return slot; // FORGE: Allow modders to set a non-default equipment slot for a stack; e.g. a non-armor chestplate-slot item
        Equipable equipable = Equipable.get(stack);
        if (equipable != null)
            return equipable.getEquipmentSlot();

        return EquipmentSlot.MAINHAND;
    }
}
