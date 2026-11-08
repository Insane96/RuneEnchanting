package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {
    @ModifyConstant(method = "brush", constant = @Constant(intValue = 10))
    private int runeenchanting$modifyRequiredBrushes(int original, @Local(argsOnly = true) Player player) {
        return RuneHooks.getRequiredBrushes(original, player);
    }

    @WrapOperation(method = "dropContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean runeenchanting$onBrushDrop(Level level, Entity entity, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        if (entity instanceof ItemEntity drop) {
            RuneHooks.onBrushDrop(player, drop);
            if (drop.getItem().isEmpty())
                return false;
        }
        return original.call(level, entity);
    }
}
