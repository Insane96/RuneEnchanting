package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class CurseOfShortArmRune extends Rune {
    @Config
    public static Double reachReduction = 0.25d;

    @Override
    public String getName() {
        return "Curse of Short Arm";
    }

    @Override
    public String getDescription() {
        return "Lowers entity and block interaction range";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(RuneEnchanting.id("curse_of_short_arm"), -reachReduction, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
        event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(RuneEnchanting.id("curse_of_short_arm"), -reachReduction, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }
}
