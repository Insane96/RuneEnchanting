package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

public class MeleeProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.40d;
    @Config(description = "Multiple runes don't increase this multiple times")
    public static Double bonusAttackSpeed = 0.1d;

    @Override
    public String getName() {
        return "Melee Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by melee attacks and increases attack speed";
    }

    @Override
    public @Nullable String getInfo() {
        return "Damage reduction: %s%%";
    }

    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && damageSource.getEntity() == damageSource.getDirectEntity();
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        if (event.getItemStack().getEquipmentSlot() == null)
            return;
        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(RuneEnchanting.id("melee_protection"), bonusAttackSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damageReduction * 100));
    }
}
