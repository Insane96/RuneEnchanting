package insane96mcp.runeenchanting.compat;

import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

/**
 * Always-safe bridge to the optional Stamina integration ({@link StaminaEvents}). Never references Stamina's
 * classes, so it loads fine when Stamina isn't installed.
 */
public class StaminaCompat {
    public static final String MOD_ID = "stamina";
    @Nullable
    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null)
            loaded = ModList.get().isLoaded(MOD_ID);
        return loaded;
    }

    /**
     * Registers the Stamina event listeners, if Stamina is installed. {@link StaminaEvents} (and its references to
     * Stamina's classes) is never loaded otherwise.
     */
    public static void init() {
        if (isLoaded())
            StaminaEvents.register();
    }
}
