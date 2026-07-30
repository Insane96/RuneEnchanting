package insane96mcp.runeenchanting.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class REUtils {
    public static float getAttackStrengthScale(Entity entity) {
        if (entity instanceof Player player) {
            float f = player.getAttackStrengthScale(0.5f);
            return f * f;
        }
        return 1f;
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
