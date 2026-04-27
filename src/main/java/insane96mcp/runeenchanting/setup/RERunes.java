package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.*;
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
    public static final ResourceKey<Registry<Rune>> REGISTRY_KEY = ResourceKey.createRegistryKey(RuneEnchanting.id("runes"));
    public static final Registry<Rune> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<Rune> RUNES = DeferredRegister.create(REGISTRY, RuneEnchanting.MOD_ID);

    private static final List<Map.Entry<String, Rune>> RUNE_ENTRIES = new ArrayList<>();

    public static final DeferredHolder<Rune, EfficiencyRune> EFFICIENCY = register("efficiency", new EfficiencyRune());
    public static final DeferredHolder<Rune, LuckRune> LUCK = register("luck", new LuckRune());
    public static final DeferredHolder<Rune, SilkTouchRune> SILK_TOUCH = register("silk_touch", new SilkTouchRune());
    public static final DeferredHolder<Rune, SharpnessRune> SHARPNESS = register("sharpness", new SharpnessRune());
    public static final DeferredHolder<Rune, BaneOfHissingRune> BANE_OF_HISSING = register("bane_of_hissing", new BaneOfHissingRune());
    public static final DeferredHolder<Rune, SmiteRune> SMITE = register("smite", new SmiteRune());
    public static final DeferredHolder<Rune, FlameRune> FLAME = register("flame", new FlameRune());
    public static final DeferredHolder<Rune, PowerRune> POWER = register("power", new PowerRune());
    public static final DeferredHolder<Rune, InfinityRune> INFINITY = register("infinity", new InfinityRune());
    public static final DeferredHolder<Rune, PiercingRune> PIERCING = register("piercing", new PiercingRune());
    public static final DeferredHolder<Rune, QuickChargeRune> QUICK_CHARGE = register("quick_charge", new QuickChargeRune());
    public static final DeferredHolder<Rune, MultishotRune> MULTISHOT = register("multishot", new MultishotRune());
    public static final DeferredHolder<Rune, RespirationRune> RESPIRATION = register("respiration", new RespirationRune());
    public static final DeferredHolder<Rune, AquaAffinityRune> AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityRune());
    public static final DeferredHolder<Rune, DepthStriderRune> DEPTH_STRIDER = register("depth_strider", new DepthStriderRune());
    public static final DeferredHolder<Rune, SwiftSneakRune> SWIFT_SNEAK = register("swift_sneak", new SwiftSneakRune());
    public static final DeferredHolder<Rune, FrostWalkerRune> FROST_WALKER = register("frost_walker", new FrostWalkerRune());
    public static final DeferredHolder<Rune, LureRune> LURE = register("lure", new LureRune());
    public static final DeferredHolder<Rune, EnduringRune> ENDURING = register("enduring", new EnduringRune());

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
