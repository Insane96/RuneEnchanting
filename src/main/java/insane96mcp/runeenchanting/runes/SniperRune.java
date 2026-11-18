package insane96mcp.runeenchanting.runes;

import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SniperRune extends Rune {
    @Override
    public String getName() {
        return "Sniper";
    }

    @Override
    public String getDescription() {
        return "Arrows fly perfectly straight";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    /**
     * Removes only the weapon's base inaccuracy, so increases from other runes (e.g. Curse of Inaccuracy) still apply
     */
    @Override
    public float modifyProjectileInaccuracy(ServerLevel level, ItemStack tool, Entity entity, float originalInaccuracy, float inaccuracy) {
        return Math.max(0f, inaccuracy - originalInaccuracy);
    }
}
