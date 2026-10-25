package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ElectrocutionRune extends Rune {
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, RuneEnchanting.id("electrocution"));
    private static final ResourceLocation NBT_PENDING_DISCHARGE = RuneEnchanting.id("electrocution/pending_discharge");

    @Config(description = "Damage the weapon must deal before its next hit discharges")
    public static Double damageToAccumulate = 20d;
    @Config(description = "How much of the weapon's accumulated damage is lost per second")
    public static Double decayPerSecond = 2d;
    @Config
    public static Double shockDamage = 3d;
    @Config(description = "Radius (in blocks) a discharge can chain within")
    public static Double radius = 4d;
    @Config(description = "How many nearby entities a discharge hits")
    public static Integer chainTargets = 4;

    @Override
    public String getName() {
        return "Electrocution";
    }

    @Override
    public String getDescription() {
        return "Damage dealt charges the weapon; once enough has accumulated the hit discharges, shocking the target and the nearby entities";
    }

    @Override
    public @Nullable String getInfo() {
        return "Damage to charge: %s, Decay: %s/s, Shock damage: %s, Chain range: %s, Chain targets: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        LivingEntity attacked = event.getEntity();
        if (target != EnchantmentTarget.ATTACKER
                || !(attacked.level() instanceof ServerLevel level)
                || event.getSource().getDirectEntity() != event.getSource().getEntity())
            return;

        charge(level, stack, attacked, event.getNewDamage(), event.getSource().getEntity());
    }

    /// Marks the bow/crossbow so that the arrows shot can find it later (they only carry a copy of the weapon).
    @Override
    public void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        UUID weaponId = stack.get(REDataComponents.ELECTROCUTION_WEAPON_ID);
        if (weaponId == null) {
            weaponId = UUID.randomUUID();
            stack.set(REDataComponents.ELECTROCUTION_WEAPON_ID, weaponId);
        }
        // The arrow copied the weapon before this is called
        ItemStack arrowWeapon = arrow.getWeaponItem();
        if (arrowWeapon != null)
            arrowWeapon.set(REDataComponents.ELECTROCUTION_WEAPON_ID, weaponId);
    }

    /// Arrows charge the weapon that shot them, as long as it's still in the shooter's inventory.
    @Override
    public void onArrowDamagePre(LivingDamageEvent.Pre event, AbstractArrow arrow, ItemStack weapon) {
        LivingEntity attacked = event.getEntity();
        UUID weaponId = weapon.get(REDataComponents.ELECTROCUTION_WEAPON_ID);
        if (!(attacked.level() instanceof ServerLevel level)
                || weaponId == null
                || !(event.getSource().getEntity() instanceof LivingEntity shooter))
            return;

        ItemStack shooterWeapon = findWeapon(shooter, weaponId);
        if (shooterWeapon.isEmpty())
            return;
        charge(level, shooterWeapon, attacked, event.getNewDamage(), shooter);
    }

    private static ItemStack findWeapon(LivingEntity entity, UUID weaponId) {
        if (entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (weaponId.equals(stack.get(REDataComponents.ELECTROCUTION_WEAPON_ID)))
                    return stack;
            }
            return ItemStack.EMPTY;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (weaponId.equals(stack.get(REDataComponents.ELECTROCUTION_WEAPON_ID)))
                return stack;
        }
        return ItemStack.EMPTY;
    }

    private static void charge(ServerLevel level, ItemStack stack, LivingEntity attacked, float damage, Entity attacker) {
        float accumulated = getAccumulatedDamage(level, stack) + damage;
        if (accumulated < damageToAccumulate) {
            stack.set(REDataComponents.ELECTROCUTION_CHARGE, accumulated);
            stack.set(REDataComponents.ELECTROCUTION_LAST_CHARGED_AT, level.getGameTime());
            return;
        }

        // The target is still inside its own hurt call here, so the discharge is deferred to its next tick
        stack.remove(REDataComponents.ELECTROCUTION_CHARGE);
        stack.remove(REDataComponents.ELECTROCUTION_LAST_CHARGED_AT);
        ModNBTData.put(attacked, NBT_PENDING_DISCHARGE, attacker.getUUID());
    }

    @Override
    public void tickTarget(ServerLevel level, LivingEntity entity) {
        if (!ModNBTData.contains(entity, NBT_PENDING_DISCHARGE))
            return;
        UUID attackerUUID = ModNBTData.get(entity, NBT_PENDING_DISCHARGE, UUID.class);
        ModNBTData.remove(entity, NBT_PENDING_DISCHARGE);

        shock(level, entity);
        Entity attacker = attackerUUID != null ? level.getEntity(attackerUUID) : null;
        List<LivingEntity> nearby = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius),
                e -> e.isAlive() && e != entity && !(e instanceof ArmorStand) && EntitySelector.NO_SPECTATORS.test(e)
                        && !e.getUUID().equals(attackerUUID) && !isAllyOf(e, attacker)));
        nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(entity)));
        for (int i = 0; i < Math.min(chainTargets, nearby.size()); i++) {
            LivingEntity next = nearby.get(i);
            spawnLine(level, entity, next);
            shock(level, next);
        }
    }

    /// The attacker's teammates and pets (or mounts) are spared by the chain.
    private static boolean isAllyOf(LivingEntity entity, @Nullable Entity attacker) {
        if (attacker == null)
            return false;
        return entity.isAlliedTo(attacker)
                || (entity instanceof OwnableEntity ownable && attacker.getUUID().equals(ownable.getOwnerUUID()));
    }

    /// Ignores invulnerability frames so the shock always lands on top of the hit that triggered it.
    /// {@code lastHurt} is restored too, otherwise the remaining invulnerability frames would only block damage above the shock damage instead of above the previous hit's.
    private static void shock(ServerLevel level, LivingEntity entity) {
        MCUtils.hurtIgnoreInvulnerability(entity, entity.damageSources().source(DAMAGE_TYPE), shockDamage.floatValue());
        spawnSparks(level, entity, 8);
    }

    /// The stored damage minus what decayed since it was last accumulated; decay is computed lazily so no per-tick update is needed.
    private static float getAccumulatedDamage(ServerLevel level, ItemStack stack) {
        if (!stack.has(REDataComponents.ELECTROCUTION_CHARGE))
            return 0f;
        long elapsed = level.getGameTime() - stack.getOrDefault(REDataComponents.ELECTROCUTION_LAST_CHARGED_AT, level.getGameTime());
        return Math.max(0f, stack.get(REDataComponents.ELECTROCUTION_CHARGE) - (float) (elapsed * decayPerSecond / 20d));
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
        return Component.translatable(getInfoTranslationKey(), damageToAccumulate, decayPerSecond, shockDamage, radius, chainTargets);
    }
}
