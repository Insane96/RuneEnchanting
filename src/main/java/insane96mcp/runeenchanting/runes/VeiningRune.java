package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

    @Config(min = 0, max = 1, description = "Mining speed multiplier")
    public static Double miningSpeedPenalty = 0.3d;

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
                .addTag(ItemTags.HOES)
                .add(Items.SHEARS);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (!stack.isCorrectToolForDrops(state)) return;

        for (BlockPos toMine : getAffectedBlocks(level, stack, pos, state)) {
            if (stack.isEmpty()) break;
            RuneHelper.breakExtraBlock(player, toMine);
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

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        return speed * miningSpeedPenalty.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), maxBlocks + 1, RuneEnchanting.NO_DECIMAL_FORMATTER.format((1f - miningSpeedPenalty) * -100));
    }
}
