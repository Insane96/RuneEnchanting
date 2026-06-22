package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class AbsorbingRune extends Rune {
    @Config(description = "Chance to completely nullify incoming direct damage", min = 0, max = 1)
    public static Double absorbingChance = 0.25d;
    @Config(description = "Multiplier applied to the nullified damage and dealt to the chestplate's durability", min = 0)
    public static Double damageToArmor = 3d;


    @Override
    public String getName() {
        return "Absorbing";
    }

    @Override
    public String getDescription() {
        return "Chance to nullify damage that will be applied to the chestplate";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public String getInfo() {
        return "Chance: %.0f%%, Armor damage: %.1fx";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), absorbingChance * 100, damageToArmor);
    }

    public boolean shouldApply(DamageSource damageSource) {
        return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && damageSource.getEntity() == damageSource.getDirectEntity();
    }

    @Override
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event, ItemStack stack, EnchantmentTarget target) {
        if (!shouldApply(event.getSource())
                || event.getAmount() <= 0
                || target != EnchantmentTarget.VICTIM)
            return;

        if (Math.random() < absorbingChance) {
            float damage = event.getAmount();
            stack.hurtAndBreak((int) (damage * damageToArmor), event.getEntity(), RuneEnchanting.getEquipmentSlotForItem(stack));
            event.setCanceled(true);
        }
    }
}
