package insane96mcp.runeenchanting.runes;

import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ExchangeRune extends Rune {
    public ExchangeRune() {
        super(0);
    }

    @Override
    public String getName() {
        return "Exchange";
    }

    @Override
    public String getDescription() {
        return "Replaces broken blocks with the ones in the off-hand";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES).addTag(ItemTags.AXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
    }

    @Override
    public void onBlockDestroyed(ServerPlayer player, ItemStack stack, BlockPos pos, BlockState state) {
        Level level = player.level();
        if (state.getDestroySpeed(level, pos) == 0)
            return;
        ItemStack offhandStack = player.getOffhandItem();
        if (!(offhandStack.getItem() instanceof BlockItem blockItem))
            return;
        ItemStack toPlace = getStackToPlace(player, offhandStack);
        //Placed as if the face opposite to the targeted one was clicked from inside the block, so e.g. slabs and stairs keep the targeted side flush
        BlockHitResult hit = RuneHelper.getTargetedBlockHit(level, player, pos, state);
        Direction direction = hit.getDirection().getOpposite();
        BlockHitResult placeHit = new BlockHitResult(hit.getLocation().add(direction.getStepX(), direction.getStepY(), direction.getStepZ()), direction, pos, hit.isInside());
        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.OFF_HAND, toPlace, placeHit);
        //If the block wasn't actually removed, the context would place the block behind it
        if (!context.replacingClickedOnBlock())
            return;
        blockItem.place(context);
    }

    ///Uses a matching stack in the inventory before the off-hand one, so the off-hand stack is consumed last
    private static ItemStack getStackToPlace(Player player, ItemStack offhandStack) {
        int slot = player.getInventory().findSlotMatchingItem(offhandStack);
        return slot == -1 ? offhandStack : player.getInventory().getItem(slot);
    }
}
