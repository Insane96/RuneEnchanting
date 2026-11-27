package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.data.provider.REBlockTagProvider;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TunnelingRune extends Rune {
    /*@Config(min = 1, max = 10, description = "Number of additional blocks to mine in the column")
    public static Integer additionalBlocks = 2;*/

    @Config(min = 0, max = 1, description = "Mining speed multiplier")
    public static Double miningSpeedPenalty = 0.4d;

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
        appender.addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(Tags.Items.TOOLS_SHEAR);
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

        if (!RuneHelper.canBreakExtraBlock(stack, state)) return;

        Direction face = RuneHelper.getTargetedBlockHit(level, player, pos, state).getDirection();

        for (BlockPos toMine : getAffectedBlocks(level, stack, player, pos, state, face)) {
            if (stack.isEmpty()) break;
            RuneHelper.breakExtraBlock(player, toMine);
        }
    }

    private static List<BlockPos> getAffectedBlocks(Level level, ItemStack stack, Player player, BlockPos targetPos, BlockState targetState, Direction face) {
        List<BlockPos> candidates = getColumnCandidates(player, targetPos, face);
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (result.size() >= additionalBlocks()) break;
            BlockState candidateState = level.getBlockState(candidate);
            //Blocks without hardness (e.g. torches) aren't mined, except the ones shears can break (e.g. grass)
            if ((candidateState.getDestroySpeed(level, candidate) > 0 || candidateState.is(REBlockTagProvider.EXTRA_BREAKABLE_WITH_SHEARS))
                    && RuneHelper.canBreakExtraBlock(stack, candidateState)
                    && targetState.getDestroySpeed(level, targetPos) >= candidateState.getDestroySpeed(level, candidate) - 0.5f) {
                result.add(candidate);
            }
        }
        return result;
    }

    private static List<BlockPos> getColumnCandidates(Player player, BlockPos targetPos, Direction face) {
        if (face == Direction.UP || face == Direction.DOWN) {
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<BlockPos> getClientAffectedBlocks(Level level, Player player, BlockPos targetPos, BlockState targetState, Direction face, Vec3 clickLocation) {
        ItemStack stack = player.getMainHandItem();
        if (!RuneHelper.canBreakExtraBlock(stack, targetState)) return List.of();

        return getAffectedBlocks(level, stack, player, targetPos, targetState, face);
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
