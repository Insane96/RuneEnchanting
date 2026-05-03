package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "doPostAttackEffectsWithItemSource", at = @At("TAIL"))
    private static void runeenchanting$doPostAttackEffectsWithItemSource(ServerLevel level, Entity attacked, DamageSource damageSource, @Nullable ItemStack itemSource, CallbackInfo ci) {
        if (attacked instanceof LivingEntity livingentity) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                RuneHooks.onPostAttack(level, livingentity.getItemBySlot(equipmentslot), EnchantmentTarget.VICTIM, attacked, damageSource);
            }
        }

        if (itemSource != null && damageSource.getEntity() instanceof LivingEntity livingDamageSource) {
            RuneHooks.onPostAttack(level, itemSource, EnchantmentTarget.ATTACKER, attacked, damageSource);
        }
    }

    @Inject(method = "onProjectileSpawned", at = @At("TAIL"))
    private static void runeenchanting$onProjectileSpawned(ServerLevel level, ItemStack firedFromWeapon, AbstractArrow arrow, Consumer<Item> onBreak, CallbackInfo ci) {
        RuneHooks.onProjectileSpawned(level, firedFromWeapon, arrow, onBreak);
    }

    @ModifyReturnValue(method = "processAmmoUse", at = @At("RETURN"))
    private static int runeenchanting$processAmmoUse(int original, ServerLevel level, ItemStack weapon, ItemStack ammo, int count) {
        return RuneHooks.modifyAmmoUse(level, weapon, ammo, original);
    }

    @ModifyReturnValue(method = "processProjectileCount", at = @At("RETURN"))
    private static int runeenchanting$processProjectileCount(int original, ServerLevel level, ItemStack tool, Entity entity, int projectileCount) {
        return RuneHooks.modifyProjectileCount(level, tool, entity, original);
    }

    @ModifyReturnValue(method = "processProjectileSpread", at = @At("RETURN"))
    private static float runeenchanting$processProjectileSpread(float original, ServerLevel level, ItemStack tool, Entity entity, float projectileSpread) {
        return RuneHooks.modifyProjectileSpread(level, tool, entity, original);
    }

    @ModifyReturnValue(method = "processDurabilityChange", at = @At("RETURN"))
    private static int runeenchanting$processDurabilityChange(int original, ServerLevel level, ItemStack stack, int damage) {
        return RuneHooks.modifyDurabilityChange(level, stack, original);
    }

    @ModifyReturnValue(method = "processBlockExperience", at = @At("RETURN"))
    private static int runeenchanting$processBlockExperience(int original, ServerLevel level, ItemStack stack, int experience) {
        return RuneHooks.modifyBlockExperience(level, stack, original);
    }

    @ModifyReturnValue(method = "processMobExperience", at = @At("RETURN"))
    private static int runeenchanting$processMobExperience(int original, ServerLevel level, @Nullable Entity killer, Entity mob, int experience) {
        return RuneHooks.modifyMobExperience(original, level, killer, mob);
    }

    @ModifyReturnValue(method = "isImmuneToDamage", at = @At("RETURN"))
    private static boolean runeenchanting$isImmuneToDamage(boolean original, ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        return original || RuneHooks.isImmuneToDamage(level, entity, damageSource);
    }

    @ModifyReturnValue(method = "getDamageProtection", at = @At("RETURN"))
    private static float runeenchanting$getDamageProtection(float original, ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        return RuneHooks.modifyDamageProtection(original, level, entity, damageSource);
    }

    @ModifyReturnValue(method = "modifyDamage", at = @At("RETURN"))
    private static float runeenchanting$modifyDamage(float original, ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float damage) {
        return RuneHooks.modifyDamage(level, tool, entity, damageSource, original);
    }

    @ModifyReturnValue(method = "modifyFallBasedDamage", at = @At("RETURN"))
    private static float runeenchanting$modifyFallBasedDamage(float original, ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float fallBasedDamage) {
        return RuneHooks.modifyFallBasedDamage(level, tool, entity, damageSource, original);
    }

    @ModifyReturnValue(method = "modifyArmorEffectiveness", at = @At("RETURN"))
    private static float runeenchanting$modifyArmorEffectiveness(float original, ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float armorEffectiveness) {
        return RuneHooks.modifyArmorEffectiveness(level, tool, entity, damageSource, original);
    }

    @ModifyReturnValue(method = "modifyKnockback", at = @At("RETURN"))
    private static float runeenchanting$modifyKnockback(float original, ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float knockback) {
        return RuneHooks.modifyKnockback(level, tool, entity, damageSource, original);
    }

    @Inject(method = "tickEffects", at = @At("TAIL"))
    private static void runeenchanting$tickEffects(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        RuneHooks.tickEffects(level, entity);
    }

    @ModifyReturnValue(method = "getPiercingCount", at = @At("RETURN"))
    private static int runeenchanting$getPiercingCount(int original, ServerLevel level, ItemStack firedFromWeapon, ItemStack pickupItemStack) {
        return RuneHooks.modifyPiercingCount(level, firedFromWeapon, pickupItemStack, original);
    }

    @Inject(method = "onHitBlock", at = @At("TAIL"))
    private static void runeenchanting$onHitBlock(ServerLevel level, ItemStack stack, @Nullable LivingEntity owner, Entity entity, @Nullable EquipmentSlot slot, Vec3 pos, BlockState state, Consumer<Item> onBreak, CallbackInfo ci) {
        RuneHooks.onHitBlock(level, stack, owner, entity, slot, pos, state, onBreak);
    }

    @ModifyReturnValue(method = "getFishingTimeReduction", at = @At("RETURN"))
    private static float runeenchanting$getFishingTimeReduction(float original, ServerLevel level, ItemStack stack, Entity entity) {
        return RuneHooks.modifyFishingTimeReduction(level, stack, entity, original);
    }

    @ModifyReturnValue(method = "getTridentReturnToOwnerAcceleration", at = @At("RETURN"))
    private static int runeenchanting$getTridentReturnToOwnerAcceleration(int original, ServerLevel level, ItemStack stack, Entity entity) {
        return RuneHooks.modifyTridentReturnToOwnerAcceleration(level, stack, entity, original);
    }

    @ModifyReturnValue(method = "modifyCrossbowChargingTime", at = @At("RETURN"))
    private static float runeenchanting$modifyCrossbowChargingTime(float original, ItemStack stack, LivingEntity entity, float crossbowChargingTime) {
        return RuneHooks.modifyCrossbowChargingTime(stack, entity, original);
    }

    @ModifyReturnValue(method = "getTridentSpinAttackStrength", at = @At("RETURN"))
    private static float runeenchanting$getTridentSpinAttackStrength(float original, ItemStack stack, LivingEntity entity) {
        return RuneHooks.modifyTridentSpinAttackStrength(stack, entity, original);
    }

    // enchantItem and enchantItemFromProvider are intentionally not cancelled: loot GLMs handle conversion.
}
