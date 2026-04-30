package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class HoppyRune extends Rune {
    @Override
    public String getName() {
        return "Hoppy";
    }

    @Override
    public String getDescription() {
        return "Increased jump height";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.JUMP_STRENGTH, new AttributeModifier(RuneEnchanting.id("hoppy"), 0.4d, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    @Override
    public void onLivingFall(LivingFallEvent event, ItemStack stack) {
        event.setDistance(event.getDistance() - 2);
    }
}
