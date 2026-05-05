package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class EnlightenedRune extends Rune {
    @Config(description = "Minimum ambient brightness in dark areas (0.0 = pitch black, 1.0 = fully bright)")
    public static Double brightness = 0.08d;

    @Override
    public String getName() { return "Enlightened"; }

    @Override
    public String getDescription() { return "Slightly increases dark vision"; }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    public static float getBrightness(@Nullable Player player, float original) {
        if (player == null || !RERunes.ENLIGHTENED.value().isEnabled())
            return original;
        return RuneHelper.hasRuneOnArmor(player, RERunes.ENLIGHTENED) ? brightness.floatValue() : original;
    }
}
