package insane96mcp.runeenchanting.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class SoulboundRune extends Rune {
    @Override
    public String getName() {
        return "Soulbound";
    }

    @Override
    public String getDescription() {
        return "The item is kept in your inventory when you die, instead of dropping";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }
}
