package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
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
