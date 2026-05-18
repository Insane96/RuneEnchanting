package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.*;
import insane96mcp.runeenchanting.runes.curse.*;
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

    // Tools
    public static final DeferredHolder<Rune, EfficiencyRune> EFFICIENCY = register("efficiency", new EfficiencyRune());
    public static final DeferredHolder<Rune, EarthbendRune> EARTHBEND = register("earthbend", new EarthbendRune());
    public static final DeferredHolder<Rune, SilkTouchRune> SILK_TOUCH = register("silk_touch", new SilkTouchRune());
    public static final DeferredHolder<Rune, VeiningRune> VEINING = register("veining", new VeiningRune());

    // Pickaxe + Shovel
    public static final DeferredHolder<Rune, ExpandedRune> EXPANDED = register("expanded", new ExpandedRune());

    // Pickaxe
    public static final DeferredHolder<Rune, BlastingRune> BLASTING = register("blasting", new BlastingRune());
    public static final DeferredHolder<Rune, DwarfingRune> DWARFING = register("dwarfing", new DwarfingRune());

    // Tools, Weapons and Fishing Rods
    public static final DeferredHolder<Rune, LuckRune> LUCK = register("luck", new LuckRune());

    // Weapons
    public static final DeferredHolder<Rune, SharpnessRune> SHARPNESS = register("sharpness", new SharpnessRune());
    public static final DeferredHolder<Rune, BaneOfHissingRune> BANE_OF_HISSING = register("bane_of_hissing", new BaneOfHissingRune());
    public static final DeferredHolder<Rune, ImpalingRune> IMPALING = register("impaling", new ImpalingRune());
    public static final DeferredHolder<Rune, WaterCoolantRune> WATER_COOLANT = register("water_coolant", new WaterCoolantRune());
    public static final DeferredHolder<Rune, SmiteRune> SMITE = register("smite", new SmiteRune());
    public static final DeferredHolder<Rune, BaneOfNosesRune> BANE_OF_NOSES = register("bane_of_noses", new BaneOfNosesRune());
    public static final DeferredHolder<Rune, RageRune> RAGE = register("rage", new RageRune());
    public static final DeferredHolder<Rune, FireSurgeRune> FIRE_SURGE = register("fire_surge", new FireSurgeRune());
    public static final DeferredHolder<Rune, SwiftStrikeRune> SWIFT_STRIKE = register("swift_strike", new SwiftStrikeRune());
    public static final DeferredHolder<Rune, AirStealerRune> AIR_STEALER = register("air_stealer", new AirStealerRune());
    public static final DeferredHolder<Rune, ExplosiveRune> EXPLOSIVE = register("explosive", new ExplosiveRune());
    public static final DeferredHolder<Rune, PartBreakerRune> PART_BREAKER = register("part_breaker", new PartBreakerRune());

    // Swords
    public static final DeferredHolder<Rune, SweepingEdgeRune> SWEEPING_EDGE = register("sweeping_edge", new SweepingEdgeRune());

    // Weapons and Bows
    public static final DeferredHolder<Rune, FlameRune> FLAME = register("flame", new FlameRune());
    public static final DeferredHolder<Rune, KnockbackRune> KNOCKBACK = register("knockback", new KnockbackRune());

    // Bows
    public static final DeferredHolder<Rune, PowerRune> POWER = register("power", new PowerRune());
    public static final DeferredHolder<Rune, InfinityRune> INFINITY = register("infinity", new InfinityRune());
    public static final DeferredHolder<Rune, GravityDefyingRune> GRAVITY_DEFYING = register("gravity_defying", new GravityDefyingRune());

    // Crossbow
    public static final DeferredHolder<Rune, PiercingRune> PIERCING = register("piercing", new PiercingRune());
    public static final DeferredHolder<Rune, QuickChargeRune> QUICK_CHARGE = register("quick_charge", new QuickChargeRune());
    public static final DeferredHolder<Rune, MultishotRune> MULTISHOT = register("multishot", new MultishotRune());

    // Armor
    public static final DeferredHolder<Rune, ProtectionRune> PROTECTION = register("protection", new ProtectionRune());
    public static final DeferredHolder<Rune, BlastProtectionRune> BLAST_PROTECTION = register("blast_protection", new BlastProtectionRune());
    public static final DeferredHolder<Rune, FireProtectionRune> FIRE_PROTECTION = register("fire_protection", new FireProtectionRune());
    public static final DeferredHolder<Rune, ProjectileProtectionRune> PROJECTILE_PROTECTION = register("projectile_protection", new ProjectileProtectionRune());
    public static final DeferredHolder<Rune, MeleeProtectionRune> MELEE_PROTECTION = register("melee_protection", new MeleeProtectionRune());
    public static final DeferredHolder<Rune, MagicProtectionRune> MAGIC_PROTECTION = register("magic_protection", new MagicProtectionRune());
    public static final DeferredHolder<Rune, HealthyRune> HEALTHY = register("healthy", new HealthyRune());
    public static final DeferredHolder<Rune, ThornsRune> THORNS = register("thorns", new ThornsRune());
    public static final DeferredHolder<Rune, MagneticRune> MAGNETIC = register("magnetic", new MagneticRune());

    // Helmet
    public static final DeferredHolder<Rune, RespirationRune> RESPIRATION = register("respiration", new RespirationRune());
    public static final DeferredHolder<Rune, AquaAffinityRune> AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityRune());
    public static final DeferredHolder<Rune, EnlightenedRune> ENLIGHTENED = register("enlightened", new EnlightenedRune());

    // Chestplate
    public static final DeferredHolder<Rune, AirAffinityRune> AIR_AFFINITY = register("air_affinity", new AirAffinityRune());
    public static final DeferredHolder<Rune, VindicationRune> VINDICATION = register("vindication", new VindicationRune());
    public static final DeferredHolder<Rune, RecoveryRune> RECOVERY = register("recovery", new RecoveryRune());
    public static final DeferredHolder<Rune, InvulnerabilityRune> INVULNERABILITY = register("invulnerability", new InvulnerabilityRune());
    public static final DeferredHolder<Rune, FireGuardianRune> FIRE_GUARDIAN = register("fire_guardian", new FireGuardianRune());

    // Leggings
    public static final DeferredHolder<Rune, SwiftSneakRune> SWIFT_SNEAK = register("swift_sneak", new SwiftSneakRune());
    public static final DeferredHolder<Rune, StepUpRune> STEP_UP = register("step_up", new StepUpRune());
    public static final DeferredHolder<Rune, ZippyRune> ZIPPY = register("zippy", new ZippyRune());
    public static final DeferredHolder<Rune, SprintPactRune> SPRINT_PACT = register("sprint_pact", new SprintPactRune());
    public static final DeferredHolder<Rune, ChargedJumpRune> CHARGED_JUMP = register("charged_jump", new ChargedJumpRune());
    public static final DeferredHolder<Rune, RetreatRune> RETREAT = register("retreat", new RetreatRune());

    // Boots
    public static final DeferredHolder<Rune, FeatherFallingRune> FEATHER_FALLING = register("feather_falling", new FeatherFallingRune());
    public static final DeferredHolder<Rune, DepthStriderRune> DEPTH_STRIDER = register("depth_strider", new DepthStriderRune());
    public static final DeferredHolder<Rune, FrostWalkerRune> FROST_WALKER = register("frost_walker", new FrostWalkerRune());
    public static final DeferredHolder<Rune, SoulSpeedRune> SOUL_SPEED = register("soul_speed", new SoulSpeedRune());
    public static final DeferredHolder<Rune, HoppyRune> HOPPY = register("hoppy", new HoppyRune());
    public static final DeferredHolder<Rune, DoubleJumpRune> DOUBLE_JUMP = register("double_jump", new DoubleJumpRune());
    public static final DeferredHolder<Rune, SteadyFallRune> STEADY_FALL = register("steady_fall", new SteadyFallRune());

    // Trident
    public static final DeferredHolder<Rune, ChannelingRune> CHANNELING = register("channeling", new ChannelingRune());
    public static final DeferredHolder<Rune, LoyaltyRune> LOYALTY = register("loyalty", new LoyaltyRune());
    public static final DeferredHolder<Rune, RiptideRune> RIPTIDE = register("riptide", new RiptideRune());

    // Fishing Rods
    public static final DeferredHolder<Rune, LureRune> LURE = register("lure", new LureRune());
    public static final DeferredHolder<Rune, JuicyBaitRune> JUICY_BAIT = register("juicy_bait", new JuicyBaitRune());

    // Tools and Weapons
    public static final DeferredHolder<Rune, AdrenalineRune> ADRENALINE = register("adrenaline", new AdrenalineRune());
    //public static final DeferredHolder<Rune, MomentumRune> MOMENTUM = register("momentum", new MomentumRune());

    // Tools, Weapons and Chestplate
    public static final DeferredHolder<Rune, ReachRune> REACH = register("reach", new ReachRune());

    // All durability items
    public static final DeferredHolder<Rune, EnduringRune> ENDURING = register("enduring", new EnduringRune());
    public static final DeferredHolder<Rune, AtmosphericRune> ATMOSPHERIC = register("atmospheric", new AtmosphericRune());

    // Curse
    public static final DeferredHolder<Rune, CurseOfBindingRune> CURSE_OF_BINDING = register("curse_of_binding", new CurseOfBindingRune());
    public static final DeferredHolder<Rune, CurseOfVanishingRune> CURSE_OF_VANISHING = register("curse_of_vanishing", new CurseOfVanishingRune());
    public static final DeferredHolder<Rune, CurseOfBloodPact> CURSE_OF_BLOOD_PACT = register("curse_of_blood_pact", new CurseOfBloodPact());
    public static final DeferredHolder<Rune, CurseOfInefficiency> CURSE_OF_INEFFICIENCY = register("curse_of_inefficiency", new CurseOfInefficiency());
    public static final DeferredHolder<Rune, CurseOfShortArmRune> CURSE_OF_SHORT_ARM = register("curse_of_short_arm", new CurseOfShortArmRune());
    public static final DeferredHolder<Rune, CurseOfUnhurriedRune> CURSE_OF_UNHURRIED = register("curse_of_unhurried", new CurseOfUnhurriedRune());
    public static final DeferredHolder<Rune, CurseOfSlowStrikeRune> CURSE_OF_SLOW_STRIKE = register("curse_of_slow_strike", new CurseOfSlowStrikeRune());
    public static final DeferredHolder<Rune, CurseOfFragility> CURSE_OF_FRAGILITY = register("curse_of_fragility", new CurseOfFragility());
    public static final DeferredHolder<Rune, CurseOfEnderRune> CURSE_OF_ENDER = register("curse_of_ender", new CurseOfEnderRune());
    public static final DeferredHolder<Rune, CurseOfSteelFall> CURSE_OF_STEEL_FALL = register("curse_of_steel_fall", new CurseOfSteelFall());
    public static final DeferredHolder<Rune, CurseOfTheVoid> CURSE_OF_THE_VOID = register("curse_of_the_void", new CurseOfTheVoid());

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
