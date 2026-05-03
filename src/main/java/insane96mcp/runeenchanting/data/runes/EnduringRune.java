package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class EnduringRune extends Rune {
    @Config(description = "Percentage bonus durability")
    public static Double bonusDurability = 1d;
    @Config
    public static Integer bonusDurabilityFlat = 80;

    @Override
    public String getName() {
        return "Enduring";
    }

    @Override
    public String getDescription() {
        return "Increases durability";
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus durability: %s%% + %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    @Override
    public int modifyDurability(int original, ItemStack stack) {
        return original + (int) (original * bonusDurability) + bonusDurabilityFlat;
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack, @Nullable Player player) {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDurability * 100), IAttributeExtension.FORMAT.format(bonusDurabilityFlat));
    }
}
