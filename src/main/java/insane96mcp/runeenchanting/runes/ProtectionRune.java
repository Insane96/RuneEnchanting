package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.Tags;

public class ProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.06d;

    @Override
    public String getName() {
        return "Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken";
    }

    @Override
    public String getInfo() {
        return "Damage reduction: %s%% (max 80%% combined)";
    }

    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && !damageSource.is(Tags.DamageTypes.IS_POISON) && !damageSource.is(Tags.DamageTypes.IS_WITHER);
    }
}
