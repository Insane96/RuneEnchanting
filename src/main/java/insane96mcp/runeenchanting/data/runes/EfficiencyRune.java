package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class EfficiencyRune extends Rune {
    @Config(description = "Percentage bonus mining speed")
    public static Double bonusMiningSpeed = 0.8d;
    @Config
    public static Double bonusFlatMiningSpeed = 2.5d;

    @Override
    public String getName() {
        return "Efficiency";
    }

    @Override
    public String getDescription() {
        return "Increases mining speed";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.SHEARS);
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
            event.addModifier(Attributes.MINING_EFFICIENCY, new AttributeModifier(RuneEnchanting.id("efficiency"), toolSpeed * bonusMiningSpeed + bonusFlatMiningSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
        }
    }
}
