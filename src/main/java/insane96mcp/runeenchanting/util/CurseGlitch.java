package insane96mcp.runeenchanting.util;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

/**
 * Purely visual "corrupted" look for curse names in tooltips: every now and then, characters are obfuscated for a moment.
 */
public class CurseGlitch {
    /** How long a glitched character stays obfuscated */
    public static final long SLOT_DURATION_MS = 250L;
    /** Time is split in windows of this many slots, each character glitches at most once per window */
    public static final int WINDOW_SLOTS = 2;
    /** The last slots of each window are always clean, so a character never glitches twice in a row */
    public static final int MIN_CLEAN_SLOTS = 1;
    /** Chance for each character to glitch in a window */
    public static final float CORRUPT_CHANCE = 0.05f;

    public static Component corrupt(Component text, ChatFormatting color) {
        long slot = Util.getMillis() / SLOT_DURATION_MS;
        long window = slot / WINDOW_SLOTS;
        int slotInWindow = (int) (slot % WINDOW_SLOTS);
        String string = text.getString();
        Style style = text.getStyle().applyFormat(color);
        MutableComponent result = Component.empty();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            Style charStyle = style;
            if (!Character.isWhitespace(c)) {
                //Xoroshiro mixes the seed, the legacy LCG gives almost the same first value for consecutive seeds
                RandomSource random = new XoroshiroRandomSource(window * 31 + i);
                if (random.nextFloat() < CORRUPT_CHANCE && random.nextInt(WINDOW_SLOTS - MIN_CLEAN_SLOTS) == slotInWindow)
                    charStyle = style.applyFormat(ChatFormatting.OBFUSCATED);
            }
            result.append(Component.literal(String.valueOf(c)).withStyle(charStyle));
        }
        return result;
    }
}
