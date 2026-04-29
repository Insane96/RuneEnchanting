package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class MagicProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.40d;
    @Config(description = "Multiple runes don't increase this multiple times")
    public static Double negativeEffectsDurationMultiplier = 0.8d;

    @Override
    public String getName() {
        return "Magic Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by magic attacks and decreases the duration of negative effects";
    }

    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && damageSource.is(Tags.DamageTypes.IS_MAGIC) && !damageSource.is(Tags.DamageTypes.IS_POISON) && !damageSource.is(Tags.DamageTypes.IS_WITHER);
    }
}
