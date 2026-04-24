package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class EfficiencyRune extends Rune {
    public EfficiencyRune() {
        super();
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        float miningSpeed = 0f;
        if (event.getItemStack().getItem() instanceof TieredItem tieredItem)
            miningSpeed = tieredItem.getTier().getSpeed();
        event.addModifier(Attributes.MINING_EFFICIENCY, new AttributeModifier(RuneEnchanting.location("efficiency"), miningSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
