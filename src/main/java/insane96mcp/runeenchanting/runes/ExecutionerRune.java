package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class ExecutionerRune extends Rune {
    public static final TagKey<EntityType<?>> RESISTANT = TagKey.create(Registries.ENTITY_TYPE, RuneEnchanting.id("executioner/resistant"));

    @Config(min = 0d, max = 1d, description = "Chance to execute an enemy at 0% health when dealing 'Base damage'. Scales linearly with the target's missing health (after the hit) and with the damage dealt")
    public static Double baseChance = 0.04d;
    @Config(min = 0d, max = 1d, description = "Same as 'Base chance' but for entities in the runeenchanting:executioner/resistant entity type tag")
    public static Double resistantChance = 0.004d;
    @Config(min = 0.1d, description = "Damage (before armor, protection and resistance) at which the chance is 'Base chance'. More damage increases the chance, less damage decreases it")
    public static Double baseDamage = 10d;
    @Config(min = 0d, max = 1d, description = "Maximum chance to execute an enemy")
    public static Double maxChance = 0.15d;
    @Config(min = 0d, description = "Multiplier of the chance for ranged attacks (arrows and thrown tridents)")
    public static Double rangedMultiplier = 0.5d;
    @Config(min = 0d, description = "Enemies in this radius around an executed entity are terrified (Weakness and Slowness). 0 disables the terror")
    public static Double terrorRadius = 6d;
    @Config(min = 0d, description = "In seconds")
    public static Double terrorDuration = 3d;
    @Config(min = 0, max = 255)
    public static Integer terrorWeaknessAmplifier = 0;
    @Config(min = 0, max = 255)
    public static Integer terrorSlownessAmplifier = 0;

    //Run after True Damage, so its bonus counts for the chance, and before Life Steal, so it heals from the executing damage
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getName() {
        return "Executioner";
    }

    @Override
    public String getDescription() {
        return "Chance to execute enemies, higher the lower their health and the higher the damage dealt. Executions terrify nearby enemies. Doesn't work on players";
    }

    @Override
    public @Nullable String getInfo() {
        return "Execute chance at 0%% health: %s%% (%s%% vs bosses) every %s damage dealt, max %s%%. Ranged attacks: x%s";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(),
                IAttributeExtension.FORMAT.format(baseChance * 100f),
                IAttributeExtension.FORMAT.format(resistantChance * 100f),
                IAttributeExtension.FORMAT.format(baseDamage),
                IAttributeExtension.FORMAT.format(maxChance * 100f),
                IAttributeExtension.FORMAT.format(rangedMultiplier));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.ATTACKER
                || !REUtils.isMeleeAttackWith(event.getSource(), stack))
            return;

        tryExecute(event, 1d);
    }

    @Override
    public void onArrowDamagePre(LivingDamageEvent.Pre event, AbstractArrow arrow, ItemStack weapon) {
        if (!(event.getSource().getEntity() instanceof LivingEntity))
            return;

        tryExecute(event, rangedMultiplier);
    }

    private static void tryExecute(LivingDamageEvent.Pre event, double multiplier) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level)
                || target instanceof Player
                || target == event.getSource().getEntity())
            return;

        //Absorption is removed after this event, so it doesn't reduce the damage yet
        float absorption = target.getAbsorptionAmount();
        float healthAfterHit = target.getHealth() - Math.max(0f, event.getNewDamage() - absorption);
        if (healthAfterHit <= 0f)
            return;

        DamageContainer container = event.getContainer();
        float unreducedDamage = event.getNewDamage()
                + container.getReduction(DamageContainer.Reduction.ARMOR)
                + container.getReduction(DamageContainer.Reduction.ENCHANTMENTS)
                + container.getReduction(DamageContainer.Reduction.MOB_EFFECTS);
        double missingHealth = 1d - Math.min(1d, healthAfterHit / target.getMaxHealth());
        double chance = target.getType().is(RESISTANT) ? resistantChance : baseChance;
        chance *= unreducedDamage / baseDamage * missingHealth * multiplier;
        chance = Math.min(chance, maxChance);
        if (chance <= 0d || target.getRandom().nextDouble() >= chance)
            return;

        //Armor, protection and resistance have already been applied, only absorption is left
        event.setNewDamage(target.getHealth() + absorption);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), RESoundEvents.EXECUTIONER.get(), SoundSource.PLAYERS, 1f, 0.5f);
        level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY(0.5d), target.getZ(), 15, target.getBbWidth() / 2f, target.getBbHeight() / 4f, target.getBbWidth() / 2f, 0.05d);
        terrify(level, target, event.getSource().getEntity());
    }

    /// Applies Weakness and Slowness to the enemies near the executed entity
    private static void terrify(ServerLevel level, LivingEntity executed, @Nullable Entity attacker) {
        int duration = (int) (terrorDuration * 20);
        if (terrorRadius <= 0d || duration <= 0)
            return;
        AABB area = executed.getBoundingBox().inflate(terrorRadius);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, e -> e instanceof Enemy && e != executed && e != attacker && e.isAlive())) {
            if (entity.distanceToSqr(executed) > terrorRadius * terrorRadius)
                continue;
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, terrorWeaknessAmplifier), attacker);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, terrorSlownessAmplifier), attacker);
            level.sendParticles(ParticleTypes.SOUL, entity.getX(), entity.getEyeY(), entity.getZ(), 3, entity.getBbWidth() / 4f, 0.1d, entity.getBbWidth() / 4f, 0.02d);
        }
    }
}
