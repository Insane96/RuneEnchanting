package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class LuckRune extends Rune {
    @Config(description = "This rune mimics Fortune, Looting and Luck of the Sea of this level")
    public static Integer enchantmentLevelEquivalent = 2;

    @Override
    public String getName() {
        return "Luck";
    }

    @Override
    public String getDescription() {
        return "Increases yield from blocks mined, entities killed and fishing";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.TRIDENT)
                .add(Items.FISHING_ROD);
    }

    @Override
    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        var registry = event.getLookup();
        registry.get(Enchantments.FORTUNE).ifPresent(fortune ->
                event.getEnchantments().set(fortune, enchantmentLevelEquivalent));
        registry.get(Enchantments.LOOTING).ifPresent(looting ->
                event.getEnchantments().set(looting, enchantmentLevelEquivalent));
        registry.get(Enchantments.LUCK_OF_THE_SEA).ifPresent(luckOfTheSea ->
                event.getEnchantments().set(luckOfTheSea, enchantmentLevelEquivalent));
    }
}
