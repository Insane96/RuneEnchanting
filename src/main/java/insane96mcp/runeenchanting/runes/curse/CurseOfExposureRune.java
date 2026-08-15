package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.setup.ILAttributes;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class CurseOfExposureRune extends Rune {
    @Config(min = 0, max = 1)
    public static Double detectionRangeIncrease = 0.4d;

    @Override
    public String getName() {
        return "Curse of Exposure";
    }

    @Override
    public String getDescription() {
        return "Increases the range at which mobs can detect you";
    }

    @Override
    public String getInfo() {
        return "Detection range increase: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(ILAttributes.MOB_DETECTION_RANGE, new AttributeModifier(RuneEnchanting.id("curse_of_exposure"), detectionRangeIncrease, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(detectionRangeIncrease * 100));
    }
}
