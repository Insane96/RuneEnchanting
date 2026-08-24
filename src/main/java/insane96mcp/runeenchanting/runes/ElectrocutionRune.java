package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class ElectrocutionRune extends Rune {
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, RuneEnchanting.id("electrocution"));
    private static final ResourceLocation NBT_SHOCK_AT = RuneEnchanting.id("electrocution/shock_at");

    @Config(description = "In seconds")
    public static Double delay = 3d;
    @Config
    public static Double shockDamage = 3d;
    @Config(description = "Radius (in blocks) a discharge can chain within")
    public static Double radius = 4d;
    @Config(description = "How many nearby entities a discharge hits")
    public static Integer chainTargets = 2;

    @Override
    public String getName() {
        return "Electrocution";
    }

    @Override
    public String getDescription() {
        return "Attacks charge the target; after a delay it discharges, shocking the target and chaining through any nearby charged entities";
    }

    @Override
    public @Nullable String getInfo() {
        return "Shock damage: %s, Delay: %ss, Chain range: %s, Chain targets: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || damageSource.getDirectEntity() != damageSource.getEntity()
                || !(attacked instanceof LivingEntity livingEntity))
            return;

        ModNBTData.put(livingEntity, NBT_SHOCK_AT, level.getGameTime() + (long) (delay * 20d));
    }

    @Override
    public void tickTarget(ServerLevel level, LivingEntity entity) {
        if (!ModNBTData.contains(entity, NBT_SHOCK_AT))
            return;

        long shockAt = ModNBTData.get(entity, NBT_SHOCK_AT, Long.class);
        if (level.getGameTime() < shockAt) {
            if ((entity.tickCount + entity.getId()) % 10 == 0)
                spawnSparks(level, entity, 1);
            return;
        }

        discharge(level, entity, new HashSet<>());
    }

    /// Damages {@code entity}, then, only if it was itself charged, consumes that charge and chains to the nearest {@link #chainTargets} unvisited entities within {@link #radius}.
    private static void discharge(ServerLevel level, LivingEntity entity, Set<LivingEntity> visited) {
        visited.add(entity);
        entity.hurt(entity.damageSources().source(DAMAGE_TYPE), shockDamage.floatValue());
        spawnSparks(level, entity, 8);

        if (!ModNBTData.contains(entity, NBT_SHOCK_AT))
            return;
        ModNBTData.remove(entity, NBT_SHOCK_AT);

        List<LivingEntity> nearby = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius),
                e -> e.isAlive() && !visited.contains(e)));
        nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(entity)));
        for (int i = 0; i < Math.min(chainTargets, nearby.size()); i++) {
            LivingEntity next = nearby.get(i);
            spawnLine(level, entity, next);
            discharge(level, next, visited);
        }
    }

    /// Uses the same particle as vanilla's lightning rod strike visual.
    private static void spawnSparks(ServerLevel level, LivingEntity entity, int count) {
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                entity.getX(), entity.getY() + entity.getBbHeight() * 0.5d, entity.getZ(),
                count, entity.getBbWidth() * 0.3d, entity.getBbHeight() * 0.3d, entity.getBbWidth() * 0.3d, 0.05d);
    }

    /// Walks a jittered line of sparks between two entities so a chained discharge visibly connects them.
    private static void spawnLine(ServerLevel level, LivingEntity from, LivingEntity to) {
        Vec3 start = from.position().add(0d, from.getBbHeight() * 0.5d, 0d);
        Vec3 end = to.position().add(0d, to.getBbHeight() * 0.5d, 0d);
        double distance = start.distanceTo(end);
        if (distance < 1.0e-4)
            return;

        Vec3 step = end.subtract(start).normalize().scale(0.15d);
        int steps = (int) (distance / 0.15d);
        RandomSource random = level.getRandom();
        for (int i = 0; i <= steps; i++) {
            Vec3 pos = start.add(step.scale(i));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    pos.x + random.nextDouble() * 0.1d - 0.05d,
                    pos.y + random.nextDouble() * 0.1d - 0.05d,
                    pos.z + random.nextDouble() * 0.1d - 0.05d,
                    1, 0d, 0d, 0d, 0d);
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), shockDamage, delay, radius, chainTargets);
    }
}
