package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class ExplosiveRune extends Rune {
    public static final ResourceLocation NBT_TIME = RuneEnchanting.id("explosive/time");
    public static final ResourceLocation NBT_POWER = RuneEnchanting.id("explosive/power");
    public static final ResourceLocation NBT_DESTROY_BLOCKS = RuneEnchanting.id("explosive/destroy_blocks");
    public static final ResourceLocation NBT_ATTACKER = RuneEnchanting.id("explosive/attacker");

    @Config(min = 0.1)
    public static Double explosionPower = 0.6d;
    @Config
    public static Boolean destroyBlocks = true;
    @Config(min = 0.05)
    public static Double delaySeconds = 3d;

    @Override
    public String getName() {
        return "Explosive";
    }

    @Override
    public String getDescription() {
        return "Attacks mark the target to explode after a delay";
    }

    @Override
    public @org.jetbrains.annotations.Nullable String getInfo() {
        return "Explosion power: %s, Delay: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || !(attacked instanceof LivingEntity))
            return;
        if (!REUtils.isAttackCharged(damageSource))
            return;
        long explodeAt = level.getGameTime() + (long) (delaySeconds * 20);
        if (!ModNBTData.contains(attacked, NBT_TIME))
            playPrimedSound(level, attacked);
        ModNBTData.put(attacked, NBT_TIME, explodeAt);
        ModNBTData.put(attacked, NBT_POWER, explosionPower.floatValue());
        ModNBTData.put(attacked, NBT_DESTROY_BLOCKS, destroyBlocks);
        ModNBTData.put(attacked, NBT_ATTACKER, damageSource.getEntity().getUUID());
    }

    @Override
    public void tickTarget(ServerLevel level, LivingEntity entity) {
        tickScheduledExplosion(level, entity);
    }

    /// Ticks the explosion scheduled on the entity, by either Explosive or Curse of Explosion (they share the same data)
    public static void tickScheduledExplosion(ServerLevel level, LivingEntity entity) {
        if (!ModNBTData.contains(entity, NBT_TIME)
                || entity.isDeadOrDying())
            return;
        long explodeAt = ModNBTData.get(entity, NBT_TIME, Long.class);
        long remaining = explodeAt - level.getGameTime();
        if (remaining > 0) {
            spawnFuseParticles(level, entity, remaining);
            return;
        }
        float power = ModNBTData.get(entity, NBT_POWER, Float.class);
        boolean shouldDestroyBlocks = ModNBTData.get(entity, NBT_DESTROY_BLOCKS, Boolean.class);
        ModNBTData.remove(entity, NBT_TIME);
        ModNBTData.remove(entity, NBT_POWER);
        ModNBTData.remove(entity, NBT_DESTROY_BLOCKS);
        UUID attackerUUID = ModNBTData.get(entity, NBT_ATTACKER, UUID.class);
        ModNBTData.remove(entity, NBT_ATTACKER);
        Entity attacker = attackerUUID != null ? level.getEntity(attackerUUID) : null;
        // The explosion has no source entity, otherwise the attacker would be excluded from its damage. The attacker is only
        // the causing entity of the damage (no direct entity, so it doesn't count as a melee hit) and the one asked for griefing
        boolean griefing = shouldDestroyBlocks && EventHooks.canEntityGrief(level, attacker);
        level.explode(null, level.damageSources().explosion(null, attacker), null, entity.getX(), entity.getY(), entity.getZ(), power, false,
                griefing ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }

    /// Fuse-like particles at the entity's feet, more frequent as the explosion approaches
    private static void spawnFuseParticles(ServerLevel level, LivingEntity entity, long remainingTicks) {
        int interval = remainingTicks > 40 ? 10 : remainingTicks > 20 ? 5 : 2;
        if (remainingTicks % interval != 0)
            return;
        double y = entity.getY() + 0.1d;
        double spread = entity.getBbWidth() / 2d;
        level.sendParticles(ParticleTypes.SMALL_FLAME, entity.getX(), y, entity.getZ(), 2, spread, 0.05d, spread, 0.04d);
        level.sendParticles(ParticleTypes.SMOKE, entity.getX(), y, entity.getZ(), 3, spread, 0.05d, spread, 0.06d);
    }

    public static void playPrimedSound(ServerLevel level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), RESoundEvents.EXPLOSIVE.get(), entity.getSoundSource(), 0.6f, 1.2f);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), explosionPower, delaySeconds);
    }
}
