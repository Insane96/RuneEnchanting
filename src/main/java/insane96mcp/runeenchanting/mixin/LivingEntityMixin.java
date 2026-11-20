package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.runeenchanting.RuneHooks;
import insane96mcp.runeenchanting.runes.InvulnerabilityRune;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = "floatValue=10.0", ordinal = 0))
    private float runeenchanting$invulnerabilityTime(float original) {
        return InvulnerabilityRune.getInvulnerableTime((LivingEntity) (Object) this, original);
    }

    @ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = "intValue=10", ordinal = 0))
    private int runeenchanting$hurtDuration(int original) {
        return InvulnerabilityRune.getHurtDuration((LivingEntity) (Object) this, original);
    }

    @ModifyExpressionValue(method = "handleDamageEvent", at = @At(value = "CONSTANT", args = "intValue=10", ordinal = 0))
    private int runeenchanting$hurtDuration2(int original) {
        return InvulnerabilityRune.getHurtDuration((LivingEntity) (Object) this, original);
    }

    @ModifyExpressionValue(method = "animateHurt", at = @At(value = "CONSTANT", args = "intValue=10", ordinal = 0))
    private int runeenchanting$hurtDuration3(int original) {
        return InvulnerabilityRune.getHurtDuration((LivingEntity) (Object) this, original);
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    private void runeenchanting$onChangedBlock(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        RuneHooks.onLocationChanged(level, (LivingEntity) (Object) this);
    }
}
