package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class FireProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.40d;
    @Config
    public static Double burningDurationReduction = 0.30d;

    @Override
    public String getName() {
        return "Fire Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by lava and fire";
    }


    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && damageSource.is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        if (event.getItemStack().getEquipmentSlot() == null)
            return;
        event.addModifier(Attributes.BURNING_TIME, new AttributeModifier(RuneEnchanting.id("fire_protection"), -burningDurationReduction, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }
}
