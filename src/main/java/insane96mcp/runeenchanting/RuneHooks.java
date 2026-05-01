package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.runeenchanting.data.runes.MagicProtectionRune;
import insane96mcp.runeenchanting.data.runes.ProjectileProtectionRune;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.mixin.MobEffectInstanceAccessor;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@LoadFeature(canBeDisabled = false)
public class RuneHooks extends Feature {
    @SubscribeEvent
    public void onStackAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().addAttributeModifiers(event);
        }
    }

    @SubscribeEvent
    public void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
        ItemStack stack = event.getStack();
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onEnchantmentLevel(event);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level))
            return;
        LivingEntity entity = event.getEntity();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                holder.value().onLivingFall(event, stack);
            }
        }
    }

    @SubscribeEvent
    public void onFollowRange(LivingEvent.LivingVisibilityEvent event) {
        if (!RuneHelper.hasRuneOnArmor(event.getEntity(), RERunes.PROJECTILE_PROTECTION))
            return;
        event.modifyVisibility(ProjectileProtectionRune.sightModifier);
    }

    @SubscribeEvent
    public void onEffectApplied(MobEffectEvent.Added event) {
        if (event.getEffectInstance().isInfiniteDuration()
                || !RuneHelper.hasRuneOnArmor(event.getEntity(), RERunes.MAGIC_PROTECTION))
            return;
        ((MobEffectInstanceAccessor) event.getEffectInstance()).setDuration((int) (event.getEffectInstance().getDuration() * MagicProtectionRune.negativeEffectsDurationMultiplier));
    }

    public static int onGetMaxDamage(int original, ItemStack stack) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int result = original;
        for (Holder<Rune> holder : runes) {
            result = holder.value().modifyDurability(result, stack);
        }
        return result;
    }

    public static float onEnchantmentDamage(float enchantmentDamage, Player player, Entity attacked, float originalDamage, DamageSource damageSource, ItemStack stack) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return originalDamage;
        float damage = originalDamage;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyEnchantmentDamage(player, attacked, damage, originalDamage, damageSource, stack);
        }
        return damage;
    }

    public static float onMiningSpeed(float original, Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float speed = original;
        for (Holder<Rune> holder : runes) {
            speed = holder.value().onMiningSpeed(player, stack, state, pos, original, speed);
        }
        return speed;
    }

    public static float onOffGroundMiningSpeedPenalty(float originalSpeedPenalty, Player player, ItemStack stack, BlockState state, BlockPos pos) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return originalSpeedPenalty;
        float speedPenalty = originalSpeedPenalty;
        for (Holder<Rune> holder : runes) {
            speedPenalty = holder.value().onOffGroundMiningSpeedPenalty(player, stack, state, pos, originalSpeedPenalty, speedPenalty);
        }
        return speedPenalty;
    }

    public static void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onProjectileSpawned(level, stack, arrow, onBreak);
        }
    }

    public static void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (stack == null)
            return;
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onPostAttack(level, stack, target, attacked, damageSource);
        }
    }

    public static int modifyAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int originalCount) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(weapon);
        if (runes == null)
            return originalCount;
        int count = originalCount;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyAmmoUse(level, weapon, ammo, originalCount, count);
        }
        return count;
    }

    public static int modifyProjectileCount(ServerLevel level, ItemStack tool, Entity entity, int originalCount) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalCount;
        int count = originalCount;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyProjectileCount(level, tool, entity, originalCount, count);
        }
        return count;
    }

    public static float modifyProjectileSpread(ServerLevel level, ItemStack tool, Entity entity, float originalSpread) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalSpread;
        float spread = originalSpread;
        for (Holder<Rune> holder : runes) {
            spread = holder.value().modifyProjectileSpread(level, tool, entity, originalSpread, spread);
        }
        return spread;
    }

    public static int modifyDurabilityChange(ServerLevel level, ItemStack stack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int damage = original;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyDurabilityChange(level, stack, original, damage);
        }
        return damage;
    }

    public static int modifyBlockExperience(ServerLevel level, ItemStack stack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int experience = original;
        for (Holder<Rune> holder : runes) {
            experience = holder.value().modifyBlockExperience(level, stack, original, experience);
        }
        return experience;
    }

    public static int modifyMobExperience(int original, ServerLevel level, @Nullable Entity killer, Entity mob) {
        if (!(killer instanceof LivingEntity livingKiller))
            return original;
        int experience = original;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = livingKiller.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                experience = holder.value().modifyMobExperience(level, stack, killer, mob, original, experience);
            }
        }
        return experience;
    }

    public static boolean isImmuneToDamage(ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                if (holder.value().isImmuneToDamage(level, stack, entity, damageSource))
                    return true;
            }
        }
        return false;
    }

    public static float modifyDamageProtection(float original, ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        float protection = original;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                protection = holder.value().modifyDamageProtection(level, stack, entity, damageSource, original, protection);
            }
        }
        return protection;
    }

    public static float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalDamage;
        float damage = originalDamage;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyDamage(level, tool, entity, damageSource, originalDamage, damage);
        }
        return damage;
    }

    public static float modifyFallBasedDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalDamage;
        float fallBasedDamage = originalDamage;
        for (Holder<Rune> holder : runes) {
            fallBasedDamage = holder.value().modifyFallBasedDamage(level, tool, entity, damageSource, originalDamage, fallBasedDamage);
        }
        return fallBasedDamage;
    }

    public static float modifyArmorEffectiveness(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalArmorEffectiveness) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalArmorEffectiveness;
        float armorEffectiveness = originalArmorEffectiveness;
        for (Holder<Rune> holder : runes) {
            armorEffectiveness = holder.value().modifyArmorEffectiveness(level, tool, entity, damageSource, originalArmorEffectiveness, armorEffectiveness);
        }
        return armorEffectiveness;
    }

    public static float modifyKnockback(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return original;
        float knockback = original;
        for (Holder<Rune> holder : runes) {
            knockback = holder.value().modifyKnockback(level, tool, entity, damageSource, original, knockback);
        }
        return knockback;
    }

    public static void tickEffects(ServerLevel level, LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                holder.value().tickEffects(level, stack, entity);
            }
        }
    }

    public static int modifyPiercingCount(ServerLevel level, ItemStack firedFromWeapon, ItemStack pickupItemStack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(firedFromWeapon);
        if (runes == null)
            return original;
        int count = original;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyPiercingCount(level, firedFromWeapon, pickupItemStack, original, count);
        }
        return count;
    }

    public static void onHitBlock(ServerLevel level, ItemStack stack, @Nullable LivingEntity owner, Entity entity, @Nullable EquipmentSlot slot, Vec3 pos, BlockState state, Consumer<Item> onBreak) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onHitBlock(level, stack, owner, entity, slot, pos, state, onBreak);
        }
    }

    public static float modifyFishingTimeReduction(ServerLevel level, ItemStack stack, Entity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float reduction = original;
        for (Holder<Rune> holder : runes) {
            reduction = holder.value().modifyFishingTimeReduction(level, stack, entity, original, reduction);
        }
        return reduction;
    }

    public static int modifyTridentReturnToOwnerAcceleration(ServerLevel level, ItemStack stack, Entity entity, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int acceleration = original;
        for (Holder<Rune> holder : runes) {
            acceleration = holder.value().modifyTridentReturnToOwnerAcceleration(level, stack, entity, original, acceleration);
        }
        return acceleration;
    }

    public static float modifyCrossbowChargingTime(ItemStack stack, LivingEntity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float chargingTime = original;
        for (Holder<Rune> holder : runes) {
            chargingTime = holder.value().modifyCrossbowChargingTime(stack, entity, original, chargingTime);
        }
        return chargingTime;
    }

    public static float modifyTridentSpinAttackStrength(ItemStack stack, LivingEntity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float strength = original;
        for (Holder<Rune> holder : runes) {
            strength = holder.value().modifyTridentSpinAttackStrength(stack, entity, original, strength);
        }
        return strength;
    }
}
