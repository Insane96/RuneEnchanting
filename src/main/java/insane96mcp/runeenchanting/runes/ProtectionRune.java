package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;

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
        return "Damage reduction: %s%% (max 80%% across multiple armor pieces)";
    }

    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }
}
