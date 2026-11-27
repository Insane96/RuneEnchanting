package insane96mcp.runeenchanting.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

/**
 * Always-safe bridge to the optional Supplementaries integration ({@link SupplementariesQuiver}). Never references
 * Supplementaries' classes, so it loads fine when Supplementaries isn't installed.
 */
public class SupplementariesCompat {
    public static final String MOD_ID = "supplementaries";
    @Nullable
    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null)
            loaded = ModList.get().isLoaded(MOD_ID);
        return loaded;
    }

    /**
     * Arrows available to the player in the given ammo stack. Supplementaries' quiver gives the weapon a 1 item copy
     * of the selected arrows instead of the real stack, so with a quiver this returns the arrows in the quiver that
     * would actually be consumed.
     */
    public static int getAvailableAmmo(Player player, ItemStack ammo) {
        if (!isLoaded())
            return ammo.getCount();
        return SupplementariesQuiver.getAvailableAmmo(player, ammo);
    }
}
