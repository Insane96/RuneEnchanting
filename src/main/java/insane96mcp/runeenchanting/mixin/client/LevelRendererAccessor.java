package insane96mcp.runeenchanting.mixin.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SortedMap;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("destroyingBlocks")
    Int2ObjectMap<BlockDestructionProgress> getDestroyingBlocks();

    @Accessor("renderBuffers")
    RenderBuffers getRenderBuffers();
}
