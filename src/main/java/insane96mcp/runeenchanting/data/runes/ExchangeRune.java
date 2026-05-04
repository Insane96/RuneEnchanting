package insane96mcp.runeenchanting.data.runes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.BlockEvent;

public class ExchangeRune extends Rune {
    @Override
    public String getName() {
        return "Exchange";
    }

    @Override
    public String getDescription() {
        return "Places the block held in the offhand in place of the mined block";
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
        if (player.isCreative()) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        ItemStack offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof BlockItem blockItem)) return;

        event.setCanceled(true);

        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        if (!state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos))) return;
        state.getBlock().destroy(level, pos, state);
        state.getBlock().playerDestroy(level, player, pos, state, blockEntity, stack);
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

        // pos is now air — BlockPlaceContext with replaceClicked=true places directly at pos
        blockItem.place(new BlockPlaceContext(level, player, InteractionHand.OFF_HAND, offhand,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
    }
}
