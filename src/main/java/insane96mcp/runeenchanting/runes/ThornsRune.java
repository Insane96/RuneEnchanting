package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import org.jetbrains.annotations.Nullable;

public class ThornsRune extends Rune {
    @Config(min = 0)
    public static Double minDamage = 1d;
    @Config(min = 0)
    public static Double maxDamage = 2d;
    @Config(min = 0, max = 1)
    public static Double chance = 0.20d;

    @Override
    public String getName() {
        return "Thorns";
    }

    @Override
    public String getDescription() {
        return "Chance to damage attackers";
    }

    @Override
    public @Nullable String getInfo() {
        return "Chance %.1f%%, Damage %.1f-%.1f";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.VICTIM
                || level.random.nextDouble() > chance
                || damageSource.getEntity() != damageSource.getDirectEntity()
                || !(damageSource.getEntity() instanceof LivingEntity attacker)
                || !(attacked instanceof LivingEntity attackedLiving))
            return;
        MCUtils.hurtIgnoreInvulnerability(attacker, attackedLiving.damageSources().thorns(attackedLiving), Mth.nextFloat(level.random, minDamage.floatValue(), maxDamage.floatValue()));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), (chance * 100), minDamage, maxDamage);
    }
}
