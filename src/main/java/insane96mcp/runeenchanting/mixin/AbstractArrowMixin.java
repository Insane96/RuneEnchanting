package insane96mcp.runeenchanting.mixin;

import insane96mcp.runeenchanting.data.runes.GravityDefyingRune;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
}
