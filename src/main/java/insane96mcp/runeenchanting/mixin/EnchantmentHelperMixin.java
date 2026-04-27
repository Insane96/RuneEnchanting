package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import insane96mcp.runeenchanting.RuneFeature;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "doPostAttackEffectsWithItemSource", at = @At("TAIL"))
    private static void onPostAttackEffectsWithItemSource(ServerLevel level, Entity attacked, DamageSource damageSource, @Nullable ItemStack itemSource, CallbackInfo ci) {
        if (attacked instanceof LivingEntity livingentity) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                RuneFeature.onPostAttack(level, livingentity.getItemBySlot(equipmentslot), EnchantmentTarget.VICTIM, attacked, damageSource);
            }
        }

        if (itemSource != null && damageSource.getEntity() instanceof LivingEntity livingDamageSource) {
            RuneFeature.onPostAttack(level, itemSource, EnchantmentTarget.ATTACKER, attacked, damageSource);
        }
    }

    @Inject(method = "onProjectileSpawned", at = @At("TAIL"))
    private static void onProjectileSpawned(ServerLevel level, ItemStack firedFromWeapon, AbstractArrow arrow, Consumer<Item> onBreak, CallbackInfo ci) {
        RuneFeature.onProjectileSpawned(level, firedFromWeapon, arrow, onBreak);
    }

    @ModifyReturnValue(method = "processAmmoUse", at = @At("RETURN"))
    private static int onProcessAmmoUse(int original, ServerLevel level, ItemStack weapon, ItemStack ammo, int count) {
        return RuneFeature.modifyAmmoUse(level, weapon, ammo, original);
    }

    @ModifyReturnValue(method = "processProjectileCount", at = @At("RETURN"))
    private static int onProcessProjectileCount(int original, ServerLevel level, ItemStack tool, Entity entity, int projectileCount) {
        return RuneFeature.modifyProjectileCount(level, tool, entity, original);
    }

    @ModifyReturnValue(method = "processProjectileSpread", at = @At("RETURN"))
    private static float onProcessProjectileSpread(float original, ServerLevel level, ItemStack tool, Entity entity, float projectileSpread) {
        return RuneFeature.modifyProjectileSpread(level, tool, entity, original);
    }
}
