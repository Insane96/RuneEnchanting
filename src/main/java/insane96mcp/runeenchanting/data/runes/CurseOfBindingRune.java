package insane96mcp.runeenchanting.data.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class CurseOfBindingRune extends Rune {
    @Override
    public String getName() {
        return "Curse of Binding";
    }

    @Override
    public String getDescription() {
        return "Prevents the item from being unequipped";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.EQUIPPABLE_ENCHANTABLE);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        event.getHolder(Enchantments.BINDING_CURSE).ifPresent(bindingCurse -> event.getEnchantments().set(bindingCurse, 1));
    }
}
