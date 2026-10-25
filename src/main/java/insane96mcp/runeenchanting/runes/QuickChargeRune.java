package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class QuickChargeRune extends Rune {
    @Config(description = "Percentage charge time reduction", min = 0, max = 1)
    public static Double chargeTimeReduction = 0.6d;

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
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public float modifyChargingTime(ItemStack stack, LivingEntity entity, float original, float chargingTime) {
        return chargingTime - original * chargeTimeReduction.floatValue();
    }

    @Override
    public @Nullable String getInfo() {
        return "Charge time reduction: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(chargeTimeReduction * 100));
    }
}
