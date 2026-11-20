package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class SoulSpeedRune extends VanillaLocationEnchantmentRune {
    @Config(description = "This rune mimics Soul Speed enchantment of this level")
    public static Integer enchantmentLevelEquivalent = 1;

    @Override
    public String getName() {
        return "Soul Speed";
    }

    @Override
    public String getDescription() {
        return "Speed up walking on soul blocks";
    }

    @Override
    public ResourceKey<Enchantment> getEnchantment() {
        return Enchantments.SOUL_SPEED;
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
