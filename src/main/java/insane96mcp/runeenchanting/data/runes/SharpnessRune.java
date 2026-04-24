package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class SharpnessRune extends Rune {
    public SharpnessRune() {
        super();
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(RuneEnchanting.location("sharpness"), 0.3d, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }
}
