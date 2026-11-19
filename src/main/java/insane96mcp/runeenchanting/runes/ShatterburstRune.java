package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.mixin.AbstractArrowAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ShatterburstRune extends Rune {
    @Config(min = 1, description = "Arrows the original arrow shatters into")
    public static Integer shardCount = 5;
    @Config(min = 0d, description = "Base damage of the shards, relative to the original arrow's base damage")
    public static Double shardDamageMultiplier = 0.5d;
    @Config(min = 0.1d, description = "Speed of the shards (a fully charged bow shoots arrows at 3)")
    public static Double shardSpeed = 1.5d;

    @Override
    public String getName() {
        return "Shatterburst";
    }

    @Override
    public String getDescription() {
        return "Arrows shatter on impact into arrows flying in random directions";
    }

    @Override
    public @Nullable String getInfo() {
        return "Shards: %s, Shard damage: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), shardCount, IAttributeExtension.FORMAT.format(shardDamageMultiplier * 100f));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onHitBlock(ServerLevel level, ItemStack stack, @Nullable LivingEntity owner, Entity entity, @Nullable EquipmentSlot slot, Vec3 pos, BlockState state, Consumer<Item> onBreak) {
        if (!(entity instanceof AbstractArrow arrow) || arrow instanceof ThrownTrident || arrow.isRemoved())
            return;
        // At this point the arrow hasn't moved yet and its motion goes up to the hit location, so trace it again to get the face hit
        Vec3 start = arrow.position();
        Vec3 end = start.add(arrow.getDeltaMovement().scale(1.1d));
        BlockHitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, arrow));
        Vec3 normal;
        Vec3 origin;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            normal = Vec3.atLowerCornerOf(hitResult.getDirection().getNormal());
            origin = hitResult.getLocation().add(normal.scale(0.1d));
        }
        else {
            normal = arrow.getDeltaMovement().normalize().reverse();
            origin = start;
        }
        shatter(level, arrow, origin, normal);
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
        shatter(level, arrow, arrow.position(), null);
    }

    /// Discards the arrow and shoots the shards from {@code origin}. With {@code normal} the shards fly only in the hemisphere it points to
    private static void shatter(ServerLevel level, AbstractArrow arrow, Vec3 origin, @Nullable Vec3 normal) {
        RandomSource random = arrow.getRandom();
        for (int i = 0; i < shardCount; i++) {
            Vec3 direction = randomDirection(random);
            if (normal != null && direction.dot(normal) < 0)
                direction = direction.reverse();
            // No weapon, so shards don't trigger Shatterburst (or other runes) again
            Arrow shard = new Arrow(level, origin.x, origin.y, origin.z, new ItemStack(Items.ARROW), null);
            shard.setOwner(arrow.getOwner());
            shard.pickup = AbstractArrow.Pickup.DISALLOWED;
            shard.setBaseDamage(arrow.getBaseDamage() * shardDamageMultiplier);
            shard.shoot(direction.x, direction.y, direction.z, shardSpeed.floatValue(), 0f);
            level.addFreshEntity(shard);
        }
        level.sendParticles(ParticleTypes.CRIT, origin.x, origin.y, origin.z, 8, 0.1d, 0.1d, 0.1d, 0.3d);
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.SHIELD_BREAK, arrow.getSoundSource(), 0.5f, 1.6f);
        arrow.discard();
    }

    private static Vec3 randomDirection(RandomSource random) {
        double y = random.nextDouble() * 2d - 1d;
        double angle = random.nextDouble() * Math.PI * 2d;
        double horizontal = Math.sqrt(1d - y * y);
        return new Vec3(Math.cos(angle) * horizontal, y, Math.sin(angle) * horizontal);
    }
}
