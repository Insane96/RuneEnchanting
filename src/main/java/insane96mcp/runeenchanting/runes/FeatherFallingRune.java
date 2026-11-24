package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class FeatherFallingRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.75d;

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .addTag(REItemTagProvider.HORSE_ARMOR);
    }

    @Override
    public String getName() {
        return "Feather Falling";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken by falls";
    }


    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }
}
