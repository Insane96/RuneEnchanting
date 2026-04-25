package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.EfficiencyRune;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.data.runes.SharpnessRune;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RERunes {
    public static final ResourceKey<Registry<Rune>> REGISTRY_KEY = ResourceKey.createRegistryKey(RuneEnchanting.location("runes"));
    public static final Registry<Rune> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<Rune> RUNES = DeferredRegister.create(REGISTRY, RuneEnchanting.MOD_ID);

    private static final List<Map.Entry<String, Rune>> RUNE_ENTRIES = new ArrayList<>();

    public static final DeferredHolder<Rune, EfficiencyRune> EFFICIENCY = register("efficiency", new EfficiencyRune(0));
    public static final DeferredHolder<Rune, SharpnessRune> SHARPNESS = register("sharpness", new SharpnessRune(0));

    private static <T extends Rune> DeferredHolder<Rune, T> register(String id, T instance) {
        RUNE_ENTRIES.add(Map.entry(id, instance));
        return RUNES.register(id, () -> instance);
    }

    public static void registerRegistry(NewRegistryEvent event) {
        event.register(RERunes.REGISTRY);
    }

    public static void registerConfigs(IEventBus modEventBus, ModContainer modContainer) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        for (var entry : RUNE_ENTRIES) {
            builder.push(entry.getKey());
            entry.getValue().loadConfig(builder);
            builder.pop();
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, builder.build(), RuneEnchanting.MOD_ID + "/runes.toml");
        modEventBus.addListener((ModConfigEvent event) -> {
            for (var entry : RUNE_ENTRIES)
                entry.getValue().readConfig();
        });
    }
}
