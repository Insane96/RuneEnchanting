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
    public static final DeferredHolder<Rune, BlastingRune> BLASTING = register("blasting", new BlastingRune());
    public static final DeferredHolder<Rune, EarthbendRune> EARTHBEND = register("earthbend", new EarthbendRune());
    public static final DeferredHolder<Rune, LuckRune> LUCK = register("luck", new LuckRune());
    public static final DeferredHolder<Rune, SilkTouchRune> SILK_TOUCH = register("silk_touch", new SilkTouchRune());
    public static final DeferredHolder<Rune, SharpnessRune> SHARPNESS = register("sharpness", new SharpnessRune());
    public static final DeferredHolder<Rune, BaneOfHissingRune> BANE_OF_HISSING = register("bane_of_hissing", new BaneOfHissingRune());
    public static final DeferredHolder<Rune, ImpalingRune> IMPALING = register("impaling", new ImpalingRune());
    public static final DeferredHolder<Rune, WaterCoolantRune> WATER_COOLANT = register("water_coolant", new WaterCoolantRune());
    public static final DeferredHolder<Rune, SmiteRune> SMITE = register("smite", new SmiteRune());
    public static final DeferredHolder<Rune, FlameRune> FLAME = register("flame", new FlameRune());
    public static final DeferredHolder<Rune, KnockbackRune> KNOCKBACK = register("knockback", new KnockbackRune());
    public static final DeferredHolder<Rune, PowerRune> POWER = register("power", new PowerRune());
    public static final DeferredHolder<Rune, InfinityRune> INFINITY = register("infinity", new InfinityRune());
    public static final DeferredHolder<Rune, PiercingRune> PIERCING = register("piercing", new PiercingRune());
    public static final DeferredHolder<Rune, QuickChargeRune> QUICK_CHARGE = register("quick_charge", new QuickChargeRune());
    public static final DeferredHolder<Rune, MultishotRune> MULTISHOT = register("multishot", new MultishotRune());
    public static final DeferredHolder<Rune, ProtectionRune> PROTECTION = register("protection", new ProtectionRune());
    public static final DeferredHolder<Rune, BlastProtectionRune> BLAST_PROTECTION = register("blast_protection", new BlastProtectionRune());
    public static final DeferredHolder<Rune, FireProtectionRune> FIRE_PROTECTION = register("fire_protection", new FireProtectionRune());
    public static final DeferredHolder<Rune, ProjectileProtectionRune> PROJECTILE_PROTECTION = register("projectile_protection", new ProjectileProtectionRune());
    public static final DeferredHolder<Rune, MeleeProtectionRune> MELEE_PROTECTION = register("melee_protection", new MeleeProtectionRune());
    public static final DeferredHolder<Rune, MagicProtectionRune> MAGIC_PROTECTION = register("magic_protection", new MagicProtectionRune());
    public static final DeferredHolder<Rune, FeatherFallingRune> FEATHER_FALLING = register("feather_falling", new FeatherFallingRune());
    public static final DeferredHolder<Rune, ThornsRune> THORNS = register("thorns", new ThornsRune());
    public static final DeferredHolder<Rune, RespirationRune> RESPIRATION = register("respiration", new RespirationRune());
    public static final DeferredHolder<Rune, AquaAffinityRune> AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityRune());
    public static final DeferredHolder<Rune, DepthStriderRune> DEPTH_STRIDER = register("depth_strider", new DepthStriderRune());
    public static final DeferredHolder<Rune, SwiftSneakRune> SWIFT_SNEAK = register("swift_sneak", new SwiftSneakRune());
    public static final DeferredHolder<Rune, FrostWalkerRune> FROST_WALKER = register("frost_walker", new FrostWalkerRune());
    public static final DeferredHolder<Rune, SoulSpeedRune> SOUL_SPEED = register("soul_speed", new SoulSpeedRune());
    public static final DeferredHolder<Rune, ChannelingRune> CHANNELING = register("channeling", new ChannelingRune());
    public static final DeferredHolder<Rune, LureRune> LURE = register("lure", new LureRune());
    public static final DeferredHolder<Rune, EnduringRune> ENDURING = register("enduring", new EnduringRune());

    public static final DeferredHolder<Rune, CurseOfBindingRune> CURSE_OF_BINDING = register("curse_of_binding", new CurseOfBindingRune());
    public static final DeferredHolder<Rune, CurseOfVanishingRune> CURSE_OF_VANISHING = register("curse_of_vanishing", new CurseOfVanishingRune());

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
        ModConfigSpec runeSpec = builder.build();
        modContainer.registerConfig(ModConfig.Type.COMMON, runeSpec, RuneEnchanting.MOD_ID + "/runes.toml");
        modEventBus.addListener((ModConfigEvent event) -> {
            if (event.getConfig().getSpec() != runeSpec)
                return;
            for (var entry : RUNE_ENTRIES)
                entry.getValue().readConfig();
        });
    }
}
