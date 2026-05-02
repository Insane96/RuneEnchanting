package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

public class BlastProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.40d;
    @Config(description = "Multiple runes don't increase this multiple times")
    public static Double explosionKnockbackReduction = 0.30d;

    @Override
    public String getName() {
        return "Blast Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage and knockback taken by explosions";
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
        return super.shouldApply(damageSource) && damageSource.is(DamageTypeTags.IS_EXPLOSION);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, new AttributeModifier(RuneEnchanting.id("blast_protection"), explosionKnockbackReduction, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damageReduction * 100));
    }
}
