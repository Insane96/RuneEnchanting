package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class SwiftSneakRune extends Rune {
    @Override
    public String getName() {
        return "Swift Sneak";
    }

    @Override
    public String getDescription() {
        return "Increases sneak movement speed";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.SNEAKING_SPEED, new AttributeModifier(RuneEnchanting.location("swift_sneak"), 0.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS);
    }
}
