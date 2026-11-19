package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;
    @Shadow @Final protected ServerPlayer player;

    //Wraps the whole method so the broken state is kept per call: extra blocks broken by runes (e.g. Veining) call destroyBlock recursively from the BreakEvent
    @WrapMethod(method = "destroyBlock")
    private boolean runeenchanting$onDestroyBlock(BlockPos pos, Operation<Boolean> original) {
        BlockState state = this.level.getBlockState(pos);
        boolean destroyed = original.call(pos);
        if (destroyed)
            RuneHooks.onBlockDestroyed(this.player, pos, state);
        return destroyed;
    }
}
