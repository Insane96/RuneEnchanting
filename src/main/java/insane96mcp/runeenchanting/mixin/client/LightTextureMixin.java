package insane96mcp.runeenchanting.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.runeenchanting.data.runes.EnlightenedRune;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "CONSTANT", args = "floatValue=0.0", ordinal = 1))
    private float runeenchanting$getBrightness(float original) {
        return EnlightenedRune.getBrightness(Minecraft.getInstance().player, original);
    }
}
