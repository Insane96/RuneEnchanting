package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.runeenchanting.RuneHooks;
import insane96mcp.runeenchanting.runes.GravityDefyingRune;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void runeenchanting$gravityDefying(CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!self.isNoGravity() || !self.getPersistentData().getBoolean(GravityDefyingRune.NBT_TAG))
            return;
        if (self.getDeltaMovement().horizontalDistance() < 0.2)
            self.setNoGravity(false);
        else
            self.setDeltaMovement(self.getDeltaMovement().scale(0.985f));
    }

    //Thrown tridents override onHitEntity, so they're not affected by this (see ThrownTridentMixin)
    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDamage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float runeenchanting$modifyEnchantmentDamage(ServerLevel level, ItemStack weapon, Entity target, DamageSource damageSource, float damage, Operation<Float> original) {
        float modifiedDamage = original.call(level, weapon, target, damageSource, damage);
        Entity owner = ((AbstractArrow) (Object) this).getOwner();
        if (!(owner instanceof LivingEntity attacker))
            return modifiedDamage;
        return RuneHooks.onEnchantmentDamage(modifiedDamage, attacker, target, damage, damageSource, weapon);
    }
}
