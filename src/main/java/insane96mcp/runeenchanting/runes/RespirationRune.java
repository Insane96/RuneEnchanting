package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class RespirationRune extends Rune {
    @Config
    public static Double oxygenBonus = 1.5d;

    @Override
    public String getName() {
        return "Respiration";
    }

    @Override
    public String getDescription() {
        return "Increases the time you can breathe underwater";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.OXYGEN_BONUS, new AttributeModifier(RuneEnchanting.id("respiration"), oxygenBonus, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD);
    }
}
