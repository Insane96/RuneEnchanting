package insane96mcp.runeenchanting.mixin.client;

import insane96mcp.runeenchanting.RuneHooksClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void runeenchanting$vibrationGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (RuneHooksClient.isVibrationGlowing(self.getId(), self.level().getGameTime()))
            cir.setReturnValue(true);
    }
}
