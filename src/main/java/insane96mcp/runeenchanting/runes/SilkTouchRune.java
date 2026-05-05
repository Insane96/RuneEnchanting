package insane96mcp.runeenchanting.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class SilkTouchRune extends Rune {
    @Override
    public String getName() {
        return "Silk Touch";
    }

    @Override
    public String getDescription() {
        return "Makes blocks drop themselves";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        var registry = event.getLookup();
        registry.get(Enchantments.SILK_TOUCH).ifPresent(silkTouch ->
                event.getEnchantments().set(silkTouch, 1));
    }
}
