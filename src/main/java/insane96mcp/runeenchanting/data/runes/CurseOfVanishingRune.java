package insane96mcp.runeenchanting.data.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class CurseOfVanishingRune extends Rune {
    @Override
    public String getName() {
        return "Curse of Vanishing";
    }

    @Override
    public String getDescription() {
        return "Destroys the item on death";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.VANISHING_ENCHANTABLE);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        event.getHolder(Enchantments.VANISHING_CURSE).ifPresent(vanishingCurse -> event.getEnchantments().set(vanishingCurse, 1));
    }
}
