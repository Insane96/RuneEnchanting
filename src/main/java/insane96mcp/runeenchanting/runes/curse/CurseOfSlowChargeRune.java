package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class CurseOfSlowChargeRune extends Rune {
    @Config(description = "Percentage charge time increase", min = 0)
    public static Double chargeTimeIncrease = 0.4d;

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
        return "Charge time increase: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public float modifyChargingTime(ItemStack stack, LivingEntity entity, float original, float chargingTime) {
        return chargingTime + original * chargeTimeIncrease.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(chargeTimeIncrease * 100));
    }
}
