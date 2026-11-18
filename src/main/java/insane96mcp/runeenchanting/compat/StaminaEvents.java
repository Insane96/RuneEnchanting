package insane96mcp.runeenchanting.compat;

import insane96mcp.runeenchanting.runes.SprintyRune;
import insane96mcp.stamina.event.StaminaEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Stamina event listeners. Only loaded through {@link StaminaCompat#init()} when Stamina is installed.
 */
public class StaminaEvents {
    static void register() {
        NeoForge.EVENT_BUS.addListener(StaminaEvents::onStaminaConsumed);
    }

    private static void onStaminaConsumed(StaminaEvent.Consumed event) {
        event.setAmount(SprintyRune.modifyStaminaConsumed(event.getEntity(), event.getAmount()));
    }
}
