package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class RageRune extends Rune {
    @Config
    public static Double maxBonusDamage = 1d;

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
        return "Bonus damage: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        return damage + (originalDamage * getBonusDamage(attacker));
    }

    public float getBonusDamage(LivingEntity attacker) {
        float ratio = Math.min(1f, 1f - ((attacker.getHealth() - 1) / attacker.getMaxHealth()));
        return maxBonusDamage.floatValue() * (ratio * ratio * 0.9f + 0.1f);
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack, @Nullable Player player) {
        if (player == null)
            return Component.empty();
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(getBonusDamage(player) * 100));
    }
}
