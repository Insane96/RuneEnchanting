package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

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

    @Override
    public @Nullable String getInfo() {
        return "Stacks %s%% of damage taken (max %s)";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(stackedDamageMultiplier * 100), IAttributeExtension.FORMAT.format(stackedDamageMax));
    }
}
