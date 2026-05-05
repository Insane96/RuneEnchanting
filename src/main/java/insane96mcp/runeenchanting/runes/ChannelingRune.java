package insane96mcp.runeenchanting.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class ChannelingRune extends Rune {
    @Override
    public String getName() {
        return "Channeling";
    }

    @Override
    public String getDescription() {
        return "Summon a lightning bolt on entity or lightning rod hit";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.TRIDENT);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        event.getHolder(Enchantments.CHANNELING).ifPresent(channeling -> event.getEnchantments().set(channeling, 1));
    }
}
