package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class FireSurgeRune extends Rune {
    @Config
    public static Double maxBonusDamage = 1d;

    @Override
    public String getName() {
        return "Fire Surge";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt when on fire. Fire resistance heavily lowers the bonus.";
    }

    @Override
    public @Nullable String getInfo() {
        return "Max bonus damage: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        return damage + (originalDamage * getBonusDamage(attacker));
    }

    public float getBonusDamage(LivingEntity attacker) {
        if (!attacker.isOnFire())
            return 0f;
        float ratio = 1;
        if (attacker.hasEffect(MobEffects.FIRE_RESISTANCE))
            ratio = 0.2f;
        return maxBonusDamage.floatValue() * ratio;
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(maxBonusDamage * 100));
    }
}
