package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public class ProjectileProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.40d;
    @Config(description = "Mobs follow range will be multiplied by this before checking if they can see the entity")
    public static Double sightModifier = 0.80d;

    @Override
    public String getName() {
        return "Projectile Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by projectiles and mobs sight";
    }


    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public boolean shouldApply(DamageSource damageSource) {
        return super.shouldApply(damageSource) && damageSource.is(DamageTypeTags.IS_PROJECTILE);
    }
}
