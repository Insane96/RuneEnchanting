package insane96mcp.runeenchanting.mixin;

import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void runeenchanting$onPlaySound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        RuneHooks.onEntitySound((Entity) (Object) this);
    }
}
