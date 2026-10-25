package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class RageRune extends Rune {
    @Config
    public static Double maxBonusDamage = 1d;
    @Config(description = "Max percentage bonus damage dealt by arrows shot with bows and crossbows")
    public static Double arrowMaxBonusDamage = 3d;

    @Override
    public String getName() {
        return "Rage";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt based on missing health";
    }

    @Override
    public @Nullable String getInfo() {
        return "Max bonus damage: %s%% (Arrows: %s%%)";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        return damage + (originalDamage * getBonusDamage(attacker, REUtils.isArrowHit(damageSource) ? arrowMaxBonusDamage : maxBonusDamage));
    }

    public float getBonusDamage(LivingEntity attacker, double maxBonusDamage) {
        float ratio = Math.min(1f, 1f - ((attacker.getHealth() - 1) / attacker.getMaxHealth()));
        return (float) maxBonusDamage * (ratio * ratio * 0.9f + 0.1f);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(maxBonusDamage * 100), RuneEnchanting.NO_DECIMAL_FORMATTER.format(arrowMaxBonusDamage * 100));
    }
}
