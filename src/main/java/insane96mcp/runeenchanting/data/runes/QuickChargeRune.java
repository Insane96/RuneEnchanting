package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import javax.annotation.Nullable;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

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

    @Override
    public @Nullable String getInfo() {
        return "Charge time reduction: %ss";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(secondsReduction));
    }
}
