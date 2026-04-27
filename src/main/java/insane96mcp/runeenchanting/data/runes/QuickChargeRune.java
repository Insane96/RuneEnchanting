package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class QuickChargeRune extends Rune {
    @Config
    public static Double secondsReduction = .75d;

    @Override
    public String getName() {
        return "Quick Charge";
    }

    @Override
    public String getDescription() {
        return "Charge arrows faster";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.CROSSBOW);
    }

    @Override
    public float modifyCrossbowChargingTime(ItemStack stack, LivingEntity entity, float original, float chargingTime) {
        return chargingTime - secondsReduction.floatValue();
    }
}
