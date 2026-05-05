package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class AdrenalineRune extends Rune {
    @Config
    public static Double maxBonusMiningSpeed = 2d;
    @Config
    public static Double maxBonusAttackSpeed = 0.6d;

    @Override
    public String getName() {
        return "Adrenaline";
    }

    @Override
    public String getDescription() {
        return "Increases mining and attack speed the lower the tool's durability";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.TRIDENT);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        Tool tool = event.getItemStack().get(DataComponents.TOOL);
        if (tool != null) {
            float toolSpeed = tool.rules().stream()
                    .filter(r -> r.correctForDrops().orElse(false) && r.speed().isPresent())
                    .map(r -> r.speed().get())
                    .max(Float::compare)
                    .orElse(tool.defaultMiningSpeed());
            event.addModifier(Attributes.MINING_EFFICIENCY, new AttributeModifier(RuneEnchanting.id("adrenaline"), toolSpeed * getSpeedBonus(event.getItemStack(), maxBonusMiningSpeed.floatValue()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
        }
        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(RuneEnchanting.id("adrenaline"), getSpeedBonus(event.getItemStack(), maxBonusAttackSpeed.floatValue()), AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    public static float getSpeedBonus(ItemStack stack, float maxBonus) {
        float durConsumed = 1f - ((float) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
        float ratio = Math.min(1f, durConsumed / 0.65f);
        return maxBonus * ratio;
    }
}
