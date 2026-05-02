package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Fixes crossbow multishot loading so that extra shots are real (pickable) arrows, and that the
 * crossbow cannot load — on either side — when the player lacks sufficient ammo.
 * <p>
 * Vanilla passes {@code j > 0} as {@code intangable} to {@code useAmmo}, making shots after the first
 * intangible (bypassing the ammo check and marking the projectile as non-pickable). Three problems
 * arise with the Multishot rune:
 * <ol>
 *   <li><b>Server phantom shots:</b> if j=0 fails (e.g. {@code processAmmoUse}=3 but only 2 arrows
 *       available), j=1 and j=2 still fire as free intangible shots.</li>
 *   <li><b>Client phantom load:</b> on the client {@code useAmmo} skips {@code processAmmoUse}
 *       (no {@code ServerLevel}), so j=0 always returns an intangible copy regardless of ammo count,
 *       creating a charged crossbow on the client even when the server refuses to load it.</li>
 *   <li><b>Non-pickable arrows:</b> extra shots are marked {@code INTANGIBLE_PROJECTILE} and cannot
 *       be picked up after landing.</li>
 * </ol>
 * Fixes applied:
 * <ul>
 *   <li>(1+2) On the client, {@code draw} is cancelled for rune-equipped crossbows, returning an
 *       empty list so the crossbow cannot load. The server sets {@code CHARGED_PROJECTILES} via the
 *       normal path and syncs it to the client.</li>
 *   <li>(1) On the server, if j=0 produced no projectile (the result list is empty), all subsequent
 *       shots also return {@code EMPTY}.</li>
 *   <li>(3) On the server, for j &gt; 0, a single real arrow is returned via
 *       {@code ammoCopy.copyWithCount(1)}, which carries no {@code INTANGIBLE_PROJECTILE} component
 *       and does not deplete the copy for subsequent iterations.</li>
 * </ul>
 */
@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private static void runeenchanting$cancelClientDraw(
        ItemStack weapon, ItemStack ammo, LivingEntity shooter,
        CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        if (!(shooter.level() instanceof ServerLevel) && RuneHelper.getRunesByPriority(weapon) != null) {
            cir.setReturnValue(List.of());
        }
    }

    @WrapOperation(
        method = "draw",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;useAmmo(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Z)Lnet/minecraft/world/item/ItemStack;")
    )
    private static ItemStack runeenchanting$guardUseAmmo(
        ItemStack weapon, ItemStack ammo, LivingEntity shooter, boolean intangable,
        Operation<ItemStack> original,
        @Local(name = "list") List<ItemStack> list,
        @Local(name = "j") int j,
        @Local(name = "itemstack1") ItemStack ammoCopy
    ) {
        if (j > 0 && list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (j > 0) {
            return ammoCopy.copyWithCount(1);
        }
        return original.call(weapon, ammo, shooter, intangable);
    }
}
