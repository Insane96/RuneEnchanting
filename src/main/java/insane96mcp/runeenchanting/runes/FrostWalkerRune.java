package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class FrostWalkerRune extends VanillaLocationEnchantmentRune {
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
    public ResourceKey<Enchantment> getEnchantment() {
        return Enchantments.FROST_WALKER;
    }

    @Override
    public int getEnchantmentLevel() {
        return enchantmentLevelEquivalent;
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(REItemTagProvider.HORSE_ARMOR).addTag(REItemTagProvider.WOLF_ARMOR);
    }
}
