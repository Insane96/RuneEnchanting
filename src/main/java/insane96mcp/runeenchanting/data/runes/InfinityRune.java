package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InfinityRune extends Rune {
    @Config(description = "This rune mimics vanilla Frost Walker of this level")
    public static Double chanceToNotUseArrow = 0.7d;

    @Override
    public String getName() {
        return "Infinity";
    }

    @Override
    public String getDescription() {
        return "Chance to not consume an arrow when using a bow";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.BOW);
    }

    @Override
    public int modifyAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int originalCount, int count) {
        float fCount = count * (1f - chanceToNotUseArrow.floatValue());
        return MathHelper.getAmountWithDecimalChance(level.random, fCount);
    }
}
