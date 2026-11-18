package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
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
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class WeakSpotRune extends Rune {
    @Config(min = 0d, description = "Percentage bonus damage dealt to targets at full health. Scales linearly with the target's current health percentage (before the hit, absorption excluded)")
    public static Double bonusDamage = 0.7d;
    @Config(min = 0d, description = "Percentage bonus damage dealt by arrows shot with bows and crossbows to targets at full health")
    public static Double arrowBonusDamage = 1d;

    @Override
    public String getName() {
        return "Weak Spot";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt the higher the target's health";
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus damage at full health: %s%% (Arrows: %s%%)";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDamage * 100), IAttributeExtension.FORMAT.format(arrowBonusDamage * 100));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!(attacked instanceof LivingEntity target) || target == attacker || target.getMaxHealth() <= 0f)
            return super.modifyEnchantmentDamage(attacker, attacked, damage, originalDamage, damageSource, stack);
        double healthRatio = Math.min(1d, target.getHealth() / target.getMaxHealth());
        double bonus = REUtils.isArrowHit(damageSource) ? arrowBonusDamage : bonusDamage;
        return (float) (damage + (originalDamage * bonus * healthRatio));
    }
}
