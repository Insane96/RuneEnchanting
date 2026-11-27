package insane96mcp.runeenchanting.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    //The first person bow drawing animation follows the bow's charging time
    @ModifyExpressionValue(method = "renderArmWithItem", at = @At(value = "CONSTANT", args = "floatValue=20.0"))
    private float runeenchanting$bowChargingTime(float original, @Local(argsOnly = true) AbstractClientPlayer player, @Local(argsOnly = true) ItemStack stack) {
        return original * RuneHooks.getBowChargingTimeMultiplier(stack, player);
    }
}
