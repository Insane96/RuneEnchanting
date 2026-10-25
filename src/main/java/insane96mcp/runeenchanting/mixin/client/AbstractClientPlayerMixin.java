package insane96mcp.runeenchanting.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    //The FOV zoom while drawing a bow follows the bow's charging time
    @ModifyExpressionValue(method = "getFieldOfViewModifier", at = @At(value = "CONSTANT", args = "floatValue=20.0"))
    private float runeenchanting$bowChargingTime(float original) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        return original * RuneHooks.getBowChargingTimeMultiplier(self.getUseItem(), self);
    }
}
