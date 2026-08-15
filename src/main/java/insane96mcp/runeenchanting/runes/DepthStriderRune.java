package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

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
    public @Nullable String getInfo() {
        return "Water movement efficiency: +%s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(waterMovementEfficiency * 100));
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
