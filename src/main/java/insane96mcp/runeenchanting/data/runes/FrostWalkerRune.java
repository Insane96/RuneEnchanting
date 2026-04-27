package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class FrostWalkerRune extends Rune {
    @Config(description = "This rune mimics vanilla Frost Walker of this level")
    public static Integer enchantmentLevelEquivalent = 3;

    @Override
    public String getName() {
        return "Frost Walker";
    }

    @Override
    public String getDescription() {
        return "Create ice when walking near water";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        event.getHolder(Enchantments.FROST_WALKER).ifPresent(frostWalker -> event.getEnchantments().set(frostWalker, enchantmentLevelEquivalent));
    }
}
