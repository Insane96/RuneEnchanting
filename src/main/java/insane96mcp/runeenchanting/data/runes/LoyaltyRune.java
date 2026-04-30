package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LoyaltyRune extends Rune {
    @Config
    public static Integer returnSpeed = 3;

    @Override
    public String getName() {
        return "Loyalty";
    }

    @Override
    public String getDescription() {
        return "Return the trident to the owner";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.TRIDENT);
    }

    @Override
    public int modifyTridentReturnToOwnerAcceleration(ServerLevel level, ItemStack stack, Entity entity, int original, int acceleration) {
        return returnSpeed;
    }
}
