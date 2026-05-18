package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class CurseOfSlowChargeRune extends Rune {
    @Config
    public static Double secondsIncrease = 0.5d;

    @Override
    public String getName() {
        return "Curse of Slow Charge";
    }

    @Override
    public String getDescription() {
        return "Charge arrows slower";
    }

    @Override
    public @Nullable String getInfo() {
        return "Charge time increase: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.CROSSBOW);
    }

    @Override
    public float modifyCrossbowChargingTime(ItemStack stack, LivingEntity entity, float original, float chargingTime) {
        return chargingTime + secondsIncrease.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(secondsIncrease));
    }
}
