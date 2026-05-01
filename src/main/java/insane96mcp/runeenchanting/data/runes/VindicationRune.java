package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REAttributes;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class VindicationRune extends Rune {
    @Config
    public static Double stackedDamageMultiplier = 0.2d;
    @Config
    public static Double stackedDamageMax = 5d;

    @Override
    public String getName() {
        return "Vindication";
    }

    @Override
    public String getDescription() {
        return "Stack damage taken and deal it back with the next attack";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        tryStackDamage(event, stack, target);
        tryApplyStackedDamage(event, stack, target);
    }

    public void tryStackDamage(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (event.getSource().getEntity() != event.getSource().getDirectEntity()
                || target != EnchantmentTarget.VICTIM)
            return;

        float stackedDamage = stack.getOrDefault(REDataComponents.STACKED_DAMAGE, 0f);
        if (stackedDamage > stackedDamageMax)
            return;
        stackedDamage += event.getNewDamage() * stackedDamageMultiplier.floatValue();
        stack.set(REDataComponents.STACKED_DAMAGE, stackedDamage);
    }

    public void tryApplyStackedDamage(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (event.getSource().getEntity() != event.getSource().getDirectEntity()
                || target != EnchantmentTarget.ATTACKER)
            return;

        float stackedDamage = stack.getOrDefault(REDataComponents.STACKED_DAMAGE, 0f);
        if (stackedDamage <= 0)
            return;
        event.setNewDamage(event.getNewDamage() + stackedDamage);
        stack.set(REDataComponents.STACKED_DAMAGE, 0f);
    }
}
