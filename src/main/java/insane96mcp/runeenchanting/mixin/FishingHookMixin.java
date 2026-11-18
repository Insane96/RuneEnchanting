package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Shadow
    @Nullable
    private Entity hookedIn;

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    //Durability consumed when pulling an hooked entity (3 for items, 5 for other entities)
    @ModifyConstant(method = "retrieve", constant = {@Constant(intValue = 3), @Constant(intValue = 5)})
    private int runeenchanting$pullDurabilityCost(int original, ItemStack stack) {
        Player player = this.getPlayerOwner();
        if (player == null || this.hookedIn == null)
            return original;
        return RuneHooks.modifyFishingPullDurabilityCost(stack, player, this.hookedIn, original);
    }

    //Strength the hooked entity is pulled towards the owner with. Runs on the client too, for hooked players
    @ModifyArg(method = "pullEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    private double runeenchanting$pullStrength(double original, @Local(argsOnly = true) Entity pulled) {
        Player player = this.getPlayerOwner();
        if (player == null)
            return original;
        ItemStack rod = player.getMainHandItem();
        if (!(rod.getItem() instanceof FishingRodItem))
            rod = player.getOffhandItem();
        if (!(rod.getItem() instanceof FishingRodItem))
            return original;
        return RuneHooks.modifyFishingPullStrength(rod, player, pulled, (float) original);
    }
}
