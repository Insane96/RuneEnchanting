package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.Rune;
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

public class CurseOfInaccuracyRune extends Rune {
    @Config
    public static Double inaccuracyIncrease = 1d;

    @Override
    public String getName() {
        return "Curse of Inaccuracy";
    }

    @Override
    public String getDescription() {
        return "Shoot arrows less accurately";
    }

    @Override
    public @Nullable String getInfo() {
        return "Inaccuracy increase: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.BOW);
        appender.add(Items.CROSSBOW);
    }

    @Override
    public float modifyProjectileInaccuracy(ServerLevel level, ItemStack tool, Entity entity, float originalInaccuracy, float inaccuracy) {
        return inaccuracy + inaccuracyIncrease.floatValue();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(inaccuracyIncrease));
    }
}
