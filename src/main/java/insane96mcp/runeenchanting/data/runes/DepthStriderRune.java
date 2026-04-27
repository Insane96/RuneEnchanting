package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class DepthStriderRune extends Rune {
    @Config
    public static Double waterMovementEfficiency = 0.75d;

    @Override
    public String getName() {
        return "Depth Strider";
    }

    @Override
    public String getDescription() {
        return "Reduces the movement speed reduction that occurs when in water";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(RuneEnchanting.id("depth_strider"), waterMovementEfficiency, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET);
    }
}
