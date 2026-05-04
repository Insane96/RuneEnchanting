package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VeiningRune extends Rune {
    @Config(min = 1, max = 256, description = "Maximum number of additional connected blocks to mine")
    public static Integer maxBlocks = 4;

    @Override
    public String getName() {
        return "Veining";
    }

    @Override
    public String getDescription() {
        return "Mines all connected blocks of the same type";
    }

    @Override
    public String getInfo() {
        return "Mined blocks: %s. Mining speed penalty: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (!stack.isCorrectToolForDrops(state)) return;

        for (BlockPos toMine : getAffectedBlocks(level, stack, pos, state)) {
            mineBlock(level, player, stack, toMine);
        }
    }

    private static List<BlockPos> getAffectedBlocks(Level level, ItemStack stack, BlockPos targetPos, BlockState targetState) {
        List<BlockPos> result = new ArrayList<>();
        Deque<BlockPos> toCheck = new ArrayDeque<>();
        Set<BlockPos> explored = new HashSet<>();
        toCheck.add(targetPos);
        explored.add(targetPos);

        while (!toCheck.isEmpty() && result.size() < maxBlocks) {
            BlockPos current = toCheck.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (explored.contains(neighbor)) continue;
                explored.add(neighbor);
                BlockState neighborState = level.getBlockState(neighbor);
                if (targetState.is(neighborState.getBlock()) && stack.isCorrectToolForDrops(neighborState)) {
                    result.add(neighbor);
                    toCheck.add(neighbor);
                    if (result.size() >= maxBlocks) break;
                }
            }
        }
        return result;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<BlockPos> getClientAffectedBlocks(Level level, Player player, BlockPos targetPos, BlockState targetState, Direction face, Vec3 clickLocation) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.isCorrectToolForDrops(targetState)) return List.of();
        return getAffectedBlocks(level, stack, targetPos, targetState);
    }

    private static void mineBlock(ServerLevel level, ServerPlayer player, ItemStack stack, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        if (!state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos))) return;
        state.getBlock().destroy(level, pos, state);
        if (!player.isCreative()) {
            state.getBlock().playerDestroy(level, player, pos, state, blockEntity, stack);
        }
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        return speed * getMiningSpeedPenalty();
    }

    public float getMiningSpeedPenalty() {
        float divider = maxBlocks + 1f;
        divider -= divider * 0.25f;
        return 1f / divider;
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), maxBlocks + 1, RuneEnchanting.NO_DECIMAL_FORMATTER.format((1f - getMiningSpeedPenalty()) * -100));
    }
}
