package insane96mcp.runeenchanting.runes;

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

import java.util.ArrayList;
import java.util.List;

public class TunnelingRune extends Rune {
    /*@Config(min = 1, max = 10, description = "Number of additional blocks to mine in the column")
    public static Integer additionalBlocks = 2;*/

    @Config(min = 0, max = 1, description = "Mining speed multiplier")
    public static Double miningSpeedPenalty = 0.3d;

    public TunnelingRune() {
        super(-1);
    }

    @Override
    public String getName() {
        return "Tunneling";
    }

    @Override
    public String getDescription() {
        return "Mines additional blocks in a column when mining";
    }

    @Override
    public String getInfo() {
        return "Mining speed penalty: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS);
    }

    public static int additionalBlocks() {
        return 2;
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (!stack.isCorrectToolForDrops(state)) return;

        for (BlockPos toMine : getAffectedBlocks(level, stack, player, pos, state)) {
            mineBlock(level, player, stack, toMine);
        }
    }

    private static List<BlockPos> getAffectedBlocks(ServerLevel level, ItemStack stack, ServerPlayer player, BlockPos targetPos, BlockState targetState) {
        List<BlockPos> candidates = getColumnCandidates(player, targetPos);
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (result.size() >= additionalBlocks()) break;
            BlockState candidateState = level.getBlockState(candidate);
            if (candidateState.getDestroySpeed(level, candidate) > 0
                    && stack.isCorrectToolForDrops(candidateState)
                    && targetState.getDestroySpeed(level, targetPos) >= candidateState.getDestroySpeed(level, candidate) - 0.5f) {
                result.add(candidate);
            }
        }
        return result;
    }

    private static List<BlockPos> getColumnCandidates(ServerPlayer player, BlockPos targetPos) {
        int blockY = targetPos.getY();
        int playerFeetY = player.getBlockY();
        if (blockY < playerFeetY || blockY > playerFeetY + 1) {
            Direction facing = player.getDirection();
            return List.of(
                targetPos.relative(facing),
                targetPos.relative(facing.getOpposite())
            );
        }
        return List.of(
            targetPos.above(),
            targetPos.below()
        );
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<BlockPos> getClientAffectedBlocks(Level level, Player player, BlockPos targetPos, BlockState targetState, Direction face, Vec3 clickLocation) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.isCorrectToolForDrops(targetState)) return List.of();

        List<BlockPos> candidates = getClientColumnCandidates(player, targetPos, face, clickLocation);
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (result.size() >= additionalBlocks()) break;
            BlockState candidateState = level.getBlockState(candidate);
            if (candidateState.getDestroySpeed(level, candidate) > 0
                    && stack.isCorrectToolForDrops(candidateState)
                    && targetState.getDestroySpeed(level, targetPos) >= candidateState.getDestroySpeed(level, candidate) - 0.5f) {
                result.add(candidate);
            }
        }
        return result;
    }

    private static List<BlockPos> getClientColumnCandidates(Player player, BlockPos targetPos, Direction face, Vec3 clickLocation) {
        if (face == Direction.UP || face == Direction.DOWN) {
            Direction playerFacing = player.getDirection();
            double fractional = clickLocation.get(playerFacing.getAxis()) - Math.floor(clickLocation.get(playerFacing.getAxis()));
            boolean forward = playerFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? (fractional > 0.5) : (fractional < 0.5);
            return List.of(
                forward ? targetPos.relative(playerFacing) : targetPos.relative(playerFacing.getOpposite()),
                forward ? targetPos.relative(playerFacing.getOpposite()) : targetPos.relative(playerFacing)
            );
        }
        double fractionalY = clickLocation.y - Math.floor(clickLocation.y);
        boolean above = fractionalY > 0.5;
        return List.of(
            above ? targetPos.above() : targetPos.below(),
            above ? targetPos.below() : targetPos.above()
        );
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        return speed * miningSpeedPenalty.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format((1f - miningSpeedPenalty) * -100));
    }
}
