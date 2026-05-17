package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class CurseOfSteelFall extends Rune {
    @Config
    public static Double damageIncrease = 0.5d;

    @Override
    public String getName() {
        return "Curse of Steel Fall";
    }

    @Override
    public String getDescription() {
        return "Increases fall damage";
    }

    @Override
    public String getInfo() {
        return "Damage increase: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM
                || !event.getSource().is(DamageTypeTags.IS_FALL))
            return;
        event.setNewDamage(event.getNewDamage() * (1f + damageIncrease.floatValue()));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(damageIncrease * 100f));
    }
}
