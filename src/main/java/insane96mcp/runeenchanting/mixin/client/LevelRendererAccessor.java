package insane96mcp.runeenchanting.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SortedMap;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("destroyingBlocks")
    SortedMap<Integer, BlockDestructionProgress> getDestroyingBlocks();

    @Accessor("renderBuffers")
    RenderBuffers getRenderBuffers();
}
