package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {
    @ModifyConstant(method = "brush", constant = @Constant(intValue = 10))
    private int runeenchanting$modifyRequiredBrushes(int original, @Local(argsOnly = true) Player player) {
        return RuneHooks.getRequiredBrushes(original, player);
    }
}
