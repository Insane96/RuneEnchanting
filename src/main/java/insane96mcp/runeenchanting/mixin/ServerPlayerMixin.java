package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.runeenchanting.runes.SprintyRune;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    // Ordinal 3 is the sprinting on ground exhaustion (after swimming, walking under water and walking on water)
    @WrapOperation(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 3))
    private void runeenchanting$modifySprintExhaustion(ServerPlayer player, float exhaustion, Operation<Void> original) {
        original.call(player, SprintyRune.modifySprintExhaustion(player, exhaustion));
    }
}
