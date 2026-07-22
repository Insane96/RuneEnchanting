package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class LureRune extends Rune {
    @Config
    public static Double secondsReduction = 12d;

    @Override
    public String getName() {
        return "Lure";
    }

    @Override
    public String getDescription() {
        return "Lowers the time it takes to lure fish";
    }

    @Override
    public @Nullable String getInfo() {
        return "Lure time reduction: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.FISHING_ROD);
    }

    @Override
    public float modifyFishingTimeReduction(ServerLevel level, ItemStack stack, Entity entity, float original, float reduction) {
        return reduction + secondsReduction.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(secondsReduction));
    }
}
