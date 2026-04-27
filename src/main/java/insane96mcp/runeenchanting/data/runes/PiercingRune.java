package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PiercingRune extends Rune {
    @Config
    public static Integer entitiesToPassThrough = 3;

    @Override
    public String getName() {
        return "Piercing";
    }

    @Override
    public String getDescription() {
        return "Arrows will pass through multiple entities";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.CROSSBOW);
    }

    @Override
    public int modifyPiercingCount(ServerLevel level, ItemStack firedFromWeapon, ItemStack pickupItemStack, int original, int count) {
        return count + entitiesToPassThrough;
    }
}
