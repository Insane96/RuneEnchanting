package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla resolves the Looting-style loot bonus from the killer's current equipment
 * ({@link EnchantmentHelper#getEnchantmentLevel(Holder, LivingEntity)}), not from the weapon that
 * actually landed the kill. For projectile kills that's wrong the moment the player swaps items
 * before the arrow's target dies, so resolve it the same way {@link RuneHooks#getKillingWeapon} does
 * for the mod's own rune hooks.
 */
@Mixin(EnchantedCountIncreaseFunction.class)
public class EnchantedCountIncreaseFunctionMixin {
    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int runeenchanting$getEnchantmentLevel(Holder<Enchantment> enchantment, LivingEntity entity, Operation<Integer> original, ItemStack stack, LootContext context) {
        DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (damageSource != null) {
            ItemStack weapon = RuneHooks.getKillingWeapon(entity, damageSource);
            if (!weapon.isEmpty())
                return EnchantmentHelper.getItemEnchantmentLevel(enchantment, weapon);
        }
        return original.call(enchantment, entity);
    }
}
