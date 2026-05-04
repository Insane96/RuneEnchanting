package insane96mcp.runeenchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import insane96mcp.insanelib.event.PlayerSprintEvent;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.mixin.client.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@EventBusSubscriber
public class RuneHooksClient {
    @SubscribeEvent
    public static void onSprintCheck(PlayerSprintEvent event) {
        LocalPlayer player = event.getPlayer();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            RuneHooks.forRunes(stack, slot, rune -> rune.onSprintCheck(event, stack));
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            RuneHooks.forRunes(stack, slot, rune -> rune.onKeyInput(event, stack));
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return;

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK)
            return;

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos();
        BlockState targetState = mc.level.getBlockState(targetPos);
        ItemStack heldStack = mc.player.getMainHandItem();

        if (!heldStack.isCorrectToolForDrops(targetState))
            return;

        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(heldStack);
        if (runes == null)
            return;

        List<BlockPos> affectedBlocks = new ArrayList<>();
        for (Holder<Rune> holder : runes) {
            affectedBlocks.addAll(holder.value().getClientAffectedBlocks(mc.level, mc.player, targetPos, targetState, blockHit.getDirection(), blockHit.getLocation()));
        }

        if (affectedBlocks.isEmpty())
            return;

        Camera camera = event.getCamera();
        double xOff = camera.getPosition().x;
        double yOff = camera.getPosition().y;
        double zOff = camera.getPosition().z;

        PoseStack poseStack = event.getPoseStack();
        LevelRendererAccessor accessor = (LevelRendererAccessor) event.getLevelRenderer();
        RenderBuffers renderBuffers = accessor.getRenderBuffers();

        renderCrackOverlay(poseStack, renderBuffers.crumblingBufferSource(), targetPos, mc.level, xOff, yOff, zOff, accessor.getDestroyingBlocks(), affectedBlocks);
        renderOutlines(poseStack, renderBuffers.bufferSource(), mc.player, mc.level, xOff, yOff, zOff, affectedBlocks);
    }

    private static void renderCrackOverlay(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, BlockPos targetPos, Level level, double xOff, double yOff, double zOff, SortedMap<Integer, BlockDestructionProgress> destroyingBlocks, List<BlockPos> affectedBlocks) {
        BlockDestructionProgress progress = null;
        for (Map.Entry<Integer, BlockDestructionProgress> entry : destroyingBlocks.entrySet()) {
            if (entry.getValue().getPos().equals(targetPos)) {
                progress = entry.getValue();
                break;
            }
        }
        if (progress == null)
            return;

        VertexConsumer crumblingConsumer = bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        for (BlockPos pos : affectedBlocks) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - xOff, pos.getY() - yOff, pos.getZ() - zOff);
            PoseStack.Pose poseEntry = poseStack.last();
            VertexConsumer blockConsumer = new SheetedDecalTextureGenerator(crumblingConsumer, poseEntry.pose(), poseEntry.normal(), 1f);
            dispatcher.renderBreakingTexture(level.getBlockState(pos), pos, level, poseStack, blockConsumer);
            poseStack.popPose();
        }
        bufferSource.endBatch();
    }

    private static void renderOutlines(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Player player, Level level, double xOff, double yOff, double zOff, List<BlockPos> affectedBlocks) {
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());
        for (BlockPos pos : affectedBlocks) {
            poseStack.pushPose();
            BlockState state = level.getBlockState(pos);
            LevelRenderer.renderShape(poseStack, lineConsumer, state.getShape(level, pos, CollisionContext.of(player)), pos.getX() - xOff, pos.getY() - yOff, pos.getZ() - zOff, 0.0F, 0.0F, 0.0F, 0.4F);
            poseStack.popPose();
        }
    }
}
