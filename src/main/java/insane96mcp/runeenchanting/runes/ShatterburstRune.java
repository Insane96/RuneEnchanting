package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.entity.ShardProjectile;
import insane96mcp.runeenchanting.mixin.AbstractArrowAccessor;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class ShatterburstRune extends Rune {
    @Config(min = 1, description = "Arrows the original arrow shatters into")
    public static Integer shardCount = 5;
    @Config(min = 0d, description = "Damage dealt by each shard")
    public static Double shardDamage = 3d;
    @Config(min = 0d, description = "Speed of the shards, relative to the original arrow's speed on impact")
    public static Double shardSpeedMultiplier = 0.5d;
    @Config(min = 0d, description = "Inaccuracy of the shards, like the bow's (a bow shoots with 1, about 1 degree of spread)")
    public static Double shardInaccuracy = 4d;

    @Override
    public String getName() {
        return "Shatterburst";
    }

    @Override
    public String getDescription() {
        return "Arrows hitting an entity shatter into a ring of shards flying around it";
    }

    @Override
    public @Nullable String getInfo() {
        return "Shards: %s, Shard damage: %s";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), shardCount, IAttributeExtension.FORMAT.format(shardDamage));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || !(damageSource.getDirectEntity() instanceof AbstractArrow arrow)
                || arrow instanceof ThrownTrident
                || arrow.isRemoved())
            return;
        // With piercing, shatter only on the last entity the arrow can pierce
        if (arrow.getPierceLevel() > 0) {
            IntOpenHashSet pierced = ((AbstractArrowAccessor) arrow).getPiercingIgnoreEntityIds();
            if (pierced == null || pierced.size() < arrow.getPierceLevel() + 1)
                return;
        }
        shatter(level, arrow, attacked);
    }

    /// Discards the arrow and shoots the shards on a horizontal ring around {@code attacked}, the first one following the arrow's horizontal direction.
    /// Shards are spawned outside the attacked entity's hitbox: it can still be invulnerable from the arrow's hit.
    private static void shatter(ServerLevel level, AbstractArrow arrow, Entity attacked) {
        // Entity hits don't change the arrow's motion before the post attack effects
        Vec3 motion = arrow.getDeltaMovement();
        float shardSpeed = (float) (motion.length() * shardSpeedMultiplier);
        double startAngle = motion.horizontalDistanceSqr() > 1.0E-7d ? Math.atan2(motion.z, motion.x) : 0d;
        Vec3 center = new Vec3(attacked.getX(), Mth.clamp(arrow.getY(), attacked.getY(), attacked.getY() + attacked.getBbHeight()), attacked.getZ());
        // Half the diagonal of the hitbox, plus some room for the shard's own hitbox
        double radius = attacked.getBbWidth() * Mth.SQRT_OF_TWO / 2f + 0.3d;
        for (int i = 0; i < shardCount; i++) {
            double angle = startAngle + Math.PI * 2d * i / shardCount;
            Vec3 direction = new Vec3(Math.cos(angle), 0d, Math.sin(angle));
            Vec3 origin = center.add(direction.scale(radius));
            // A wall between the entity and the spawn point: the shard would fly straight into it anyway
            if (level.clip(new ClipContext(center, origin, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, arrow)).getType() != HitResult.Type.MISS)
                continue;
            ShardProjectile shard = new ShardProjectile(level, origin.x, origin.y, origin.z);
            shard.setOwner(arrow.getOwner());
            shard.shoot(direction.x, direction.y, direction.z, shardSpeed, shardInaccuracy.floatValue());
            level.addFreshEntity(shard);
        }
        level.sendParticles(ParticleTypes.CRIT, arrow.getX(), arrow.getY(), arrow.getZ(), 8, 0.1d, 0.1d, 0.1d, 0.3d);
        level.playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(), RESoundEvents.SHATTERBURST.get(), arrow.getSoundSource(), 0.5f, 1.6f);
        arrow.discard();
    }
}
