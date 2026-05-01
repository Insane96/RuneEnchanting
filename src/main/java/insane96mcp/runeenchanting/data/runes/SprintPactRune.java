package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.event.PlayerSprintEvent;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class SprintPactRune extends Rune {
    @Override
    public String getName() {
        return "Sprint Pact";
    }

    @Override
    public String getDescription() {
        return "Increased movement speed but can no longer sprint";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(RuneEnchanting.id("sprint_pact"), 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    @Override
    public void onSprintCheck(PlayerSprintEvent event, ItemStack stack) {
        event.setCanceled(true);
    }
}
