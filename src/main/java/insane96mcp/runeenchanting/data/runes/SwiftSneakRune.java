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

public class SwiftSneakRune extends Rune {
    @Config
    public static Double bonusMovementSpeed = 0.4d;

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
        event.addModifier(Attributes.SNEAKING_SPEED, new AttributeModifier(RuneEnchanting.id("swift_sneak"), bonusMovementSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS);
    }
}
