package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LureRune extends Rune {
    @Config
    public static Double secondsReduction = 15d;

    @Override
    public String getName() {
        return "Lure";
    }

    @Override
    public String getDescription() {
        return "Lowers the time it takes to lure fish";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.FISHING_ROD);
    }

    @Override
    public float modifyFishingTimeReduction(ServerLevel level, ItemStack stack, Entity entity, float original, float reduction) {
        return reduction + secondsReduction.floatValue();
    }
}
