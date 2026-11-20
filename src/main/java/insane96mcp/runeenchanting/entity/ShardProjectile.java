package insane96mcp.runeenchanting.entity;

import insane96mcp.runeenchanting.runes.ShatterburstRune;
import insane96mcp.runeenchanting.setup.REEntityTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/// Shatterburst's shards: a snowball-like projectile dealing {@link ShatterburstRune#shardDamage}, breaking on whatever it hits
public class ShardProjectile extends ThrowableItemProjectile {
    public ShardProjectile(EntityType<? extends ShardProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ShardProjectile(Level level, double x, double y, double z) {
        super(REEntityTypes.SHARD.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLINT;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, this.getItem());
            for (int i = 0; i < 8; i++)
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0d, 0d, 0d);
        }
        else
            super.handleEntityEvent(id);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), ShatterburstRune.shardDamage.floatValue());
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
