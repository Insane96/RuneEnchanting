package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class AirStealerRune extends Rune {
    @Config(min = 1, description = "This scales based off attack speed")
    public static Double secondsStolen = 2d;

    @Override
    public String getName() {
        return "Air Stealer";
    }

    @Override
    public String getDescription() {
        return "Steals air supply from the attacked entity, slower/faster weapons steal more/less.";
    }

    @Override
    public @Nullable String getInfo() {
        return "Air stolen at 1.0 attack speed: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || !(attacked instanceof LivingEntity livingAttacked)
                || !(damageSource.getEntity() instanceof LivingEntity attacker))
            return;

        float attackCooldown = REUtils.getAttackStrengthScale(attacker);
        double attackSpeedMod = 1.0 / attacker.getAttributeValue(Attributes.ATTACK_SPEED);
        int ticks = (int) (secondsStolen * 20d * attackCooldown * attackSpeedMod);
        if (ticks <= 0)
            return;

        livingAttacked.setAirSupply(livingAttacked.getAirSupply() - ticks);
        attacker.setAirSupply(attacker.getAirSupply() + ticks);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(secondsStolen));
    }
}
