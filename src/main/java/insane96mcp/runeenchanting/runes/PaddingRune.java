package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class PaddingRune extends Rune {
    @Config(min = 0d, max = 1d)
    public static Double knockbackReduction = 0.5d;

    public PaddingRune() {
        super(1);
    }

    @Override
    public String getName() {
        return "Padding";
    }

    @Override
    public String getDescription() {
        return "Reduces knockback dealt by weapons";
    }

    @Override
    public @Nullable String getInfo() {
        return "Knockback reduction: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public float modifyMeleeKnockback(ServerLevel level, ItemStack tool, LivingEntity attacker, LivingEntity target, float knockback) {
        return knockback * (1f - knockbackReduction.floatValue());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(knockbackReduction * 100));
    }
}
