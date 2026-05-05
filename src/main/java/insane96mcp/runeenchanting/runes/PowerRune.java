package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class PowerRune extends Rune {
    @Config(description = "Percentage bonus damage")
    public static Double bonusDamage = 1d;

    @Override
    public String getName() {
        return "Power";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt by the arrow";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.BOW);
    }

    @Override
    public float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage, float damage) {
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow))
            return damage;
        return (float) (damage + (originalDamage * bonusDamage));
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus damage: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDamage * 100));
    }
}
