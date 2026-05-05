package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class RiptideRune extends Rune {
    @Config(description = "This rune mimics its enchantment counterpart of this level")
    public static Integer enchantmentLevelEquivalent = 3;

    @Override
    public String getName() {
        return "Riptide";
    }

    @Override
    public String getDescription() {
        return "Throw yourself instead of the trident and deal damage on contact";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.TRIDENT);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        event.getHolder(Enchantments.RIPTIDE).ifPresent(riptide -> event.getEnchantments().set(riptide, enchantmentLevelEquivalent));
    }
}
