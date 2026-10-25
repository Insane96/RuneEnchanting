package insane96mcp.runeenchanting.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class REUtils {
    public static float getAttackStrengthScale(Entity entity) {
        if (entity instanceof Player player) {
            float f = player.getAttackStrengthScale(0.5f);
            return f * f;
        }
        return 1f;
    }

    /**
     * Like {@link #getAttackStrengthScale(Entity)}, but projectiles (arrows and thrown tridents) always count as fully charged, as the attack cooldown is irrelevant for them.
     */
    public static float getAttackStrengthScale(DamageSource damageSource) {
        if (damageSource.getDirectEntity() != damageSource.getEntity())
            return 1f;
        return getAttackStrengthScale(damageSource.getEntity());
    }

    /**
     * True if the damage was dealt by an arrow shot from a bow or crossbow. Thrown tridents are excluded, as they deal their damage based off the item's attack damage
     */
    public static boolean isArrowHit(DamageSource damageSource) {
        return damageSource.getDirectEntity() instanceof AbstractArrow
                && !(damageSource.getDirectEntity() instanceof ThrownTrident);
    }

    public static boolean isAttackCharged(DamageSource damageSource) {
        Entity entity = damageSource.getEntity();
        if (!(entity instanceof Player player))
            return true;
        if (damageSource.getDirectEntity() != entity)
            return true; // Hit by a projectile/thrown weapon (e.g. a thrown trident), not a melee swing: the attack cooldown is irrelevant.
        return player.getAttackStrengthScale(0.5f) >= 0.9f;
    }
}
