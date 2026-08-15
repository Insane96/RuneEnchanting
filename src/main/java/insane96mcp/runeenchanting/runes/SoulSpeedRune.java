package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

import javax.annotation.Nullable;

public class SoulSpeedRune extends Rune {
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
    public @Nullable String getInfo() {
        return "Equivalent to enchantment level %s";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), enchantmentLevelEquivalent);
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        var registry = event.getLookup();
        registry.get(Enchantments.SOUL_SPEED).ifPresent(soulSpeed ->
                event.getEnchantments().set(soulSpeed, enchantmentLevelEquivalent));
    }
}
