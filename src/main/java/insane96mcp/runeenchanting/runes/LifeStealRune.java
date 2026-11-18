package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class LifeStealRune extends Rune {
    @Config(min = 0d, description = "Percentage of the damage dealt (after armor and resistances, capped to the target's remaining health) healed")
    public static Double healPercentage = 0.05d;

    @Override
    public String getName() {
        return "Life Steal";
    }

    @Override
    public String getDescription() {
        return "Heals for a percentage of the damage dealt";
    }

    @Override
    public @Nullable String getInfo() {
        return "Heal: %s%% of damage dealt";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(healPercentage * 100f));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.ATTACKER
                || event.getSource().getDirectEntity() != event.getSource().getEntity()
                || !REUtils.isMeleeWeapon(stack)
                || !(event.getSource().getEntity() instanceof LivingEntity attacker))
            return;

        heal(attacker, event);
    }

    @Override
    public void onArrowDamagePre(LivingDamageEvent.Pre event, AbstractArrow arrow, ItemStack weapon) {
        if (!(event.getSource().getEntity() instanceof LivingEntity shooter))
            return;

        heal(shooter, event);
    }

    private static void heal(LivingEntity healed, LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        if (healed.level().isClientSide() || healed == attacked)
            return;
        float damage = Math.min(event.getNewDamage(), attacked.getHealth() + attacked.getAbsorptionAmount());
        float amount = (float) (damage * healPercentage);
        if (amount > 0f)
            healed.heal(amount);
    }
}
