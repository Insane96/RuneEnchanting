package insane96mcp.runeenchanting.util;

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

    public static boolean isAttackCharged(Entity entity) {
        if (entity instanceof Player player)
            return player.getAttackStrengthScale(0.5f) >= 0.9f;
        return true;
    }
}
