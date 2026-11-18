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
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class TrueDamageRune extends Rune {
    @Config(min = 0d, description = "Percentage of the damage dealt (before armor, protection and resistance) added as damage that ignores them")
    public static Double bonusPercentage = 0.15d;

    @Override
    public String getName() {
        return "True Damage";
    }

    @Override
    public String getDescription() {
        return "Deals extra damage that ignores armor, protection and resistance";
    }

    @Override
    public @Nullable String getInfo() {
        return "True damage: %s%% of damage dealt";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusPercentage * 100f));
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
                || !(event.getSource().getEntity() instanceof LivingEntity))
            return;

        addTrueDamage(event);
    }

    @Override
    public void onArrowDamagePre(LivingDamageEvent.Pre event, AbstractArrow arrow, ItemStack weapon) {
        addTrueDamage(event);
    }

    /// The bonus is based on the damage before armor, enchantments and mob effects reductions, but after the shield and invulnerability ones, so blocked damage doesn't count.
    private static void addTrueDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide() || event.getEntity() == event.getSource().getEntity())
            return;
        DamageContainer container = event.getContainer();
        float unreducedDamage = event.getNewDamage()
                + container.getReduction(DamageContainer.Reduction.ARMOR)
                + container.getReduction(DamageContainer.Reduction.ENCHANTMENTS)
                + container.getReduction(DamageContainer.Reduction.MOB_EFFECTS);
        float bonus = (float) (unreducedDamage * bonusPercentage);
        if (bonus > 0f)
            event.setNewDamage(event.getNewDamage() + bonus);
    }
}
