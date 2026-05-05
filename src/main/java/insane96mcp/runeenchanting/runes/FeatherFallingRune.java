package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public class FeatherFallingRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.75d;

    @Override
    public String getName() {
        return "Feather Falling";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by falls";
    }


    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && damageSource.is(DamageTypeTags.IS_FALL);
    }
}
