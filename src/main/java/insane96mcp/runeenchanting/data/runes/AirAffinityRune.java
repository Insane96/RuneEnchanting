package insane96mcp.runeenchanting.data.runes;

import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class AirAffinityRune extends Rune {
    @Override
    public String getName() {
        return "Air Affinity";
    }

    @Override
    public String getDescription() {
        return "Negates the mining speed penalty when off ground";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public float onOffGroundMiningSpeedPenalty(Player player, ItemStack stack, BlockState state, BlockPos pos, float original, float speedPenalty) {
        return 1f;
    }
}
