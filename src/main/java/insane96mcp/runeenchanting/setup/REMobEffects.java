package insane96mcp.runeenchanting.setup;

import insane96mcp.insanelib.world.effect.ILMobEffect;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(Registries.MOB_EFFECT, RuneEnchanting.MOD_ID);

    private static final int MOMENTUM_COLOR = 0xFCD373;
    // Bonuses per effect level, multiplied by the level (amplifier + 1)
    public static final double MINING_MOMENTUM_PER_LEVEL = 0.05d;
    public static final double MOVEMENT_MOMENTUM_PER_LEVEL = 0.04d;
    public static final double ATTACK_MOMENTUM_PER_LEVEL = 0.04d;

    public static final DeferredHolder<MobEffect, MobEffect> MINING_MOMENTUM = REGISTRY.register("mining_momentum",
            () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, MOMENTUM_COLOR, false)
                    .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, RuneEnchanting.id("effect.mining_momentum"), MINING_MOMENTUM_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredHolder<MobEffect, MobEffect> MOVEMENT_MOMENTUM = REGISTRY.register("movement_momentum",
            () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, MOMENTUM_COLOR, false)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, RuneEnchanting.id("effect.movement_momentum"), MOVEMENT_MOMENTUM_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredHolder<MobEffect, MobEffect> ATTACK_MOMENTUM = REGISTRY.register("attack_momentum",
            () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, MOMENTUM_COLOR, false)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, RuneEnchanting.id("effect.attack_momentum"), ATTACK_MOMENTUM_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
}
