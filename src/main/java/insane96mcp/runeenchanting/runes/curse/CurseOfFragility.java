package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfFragility extends Rune {

    @Config(min = 0, max = 1)
    public static Double durabilityMultiplier = 0.5d;

    @Override
    public String getName() {
        return "Curse of Fragility";
    }

    @Override
    public String getDescription() {
        return "Decreases item's durability";
    }

    @Override
    public String getInfo() {
        return "Durability penalty: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    //Run after all the other durability runes
    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public int modifyDurability(int original, ItemStack stack) {
        return (int) (original * durabilityMultiplier.floatValue());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(durabilityMultiplier * 100f));
    }
}
