package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnduringRune extends Rune {
    @Config(description = "Percentage bonus durability")
    public static Double bonusDurability = 1d;
    @Config
    public static Integer bonusDurabilityFlat = 80;

    @Override
    public String getName() {
        return "Enduring";
    }

    @Override
    public String getDescription() {
        return "Increases durability";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    @Override
    public int modifyDurability(int original, ItemStack stack) {
        return original + (int) (original * bonusDurability) + bonusDurabilityFlat;
    }
}
