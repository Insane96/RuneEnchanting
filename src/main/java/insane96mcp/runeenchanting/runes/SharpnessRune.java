package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

public class SharpnessRune extends Rune {
    @Config
    public static Double bonusDamage = 0.3d;
    @Config(description = "Percentage bonus damage dealt by arrows shot with bows and crossbows")
    public static Double arrowBonusDamage = 1d;

    @Override
    public String getName() {
        return "Sharpness";
    }

    @Override
    public String getDescription() {
        return "Increases attack damage and damage dealt by arrows";
    }

    @Override
    public @Nullable String getInfo() {
        return "Attack damage: +%s%% (Arrows: +%s%%)";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDamage * 100), IAttributeExtension.FORMAT.format(arrowBonusDamage * 100));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        //Bows and crossbows aren't melee weapons, their bonus is applied to arrows
        if (event.getItemStack().getItem() instanceof ProjectileWeaponItem)
            return;
        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(RuneEnchanting.id("sharpness"), bonusDamage, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }

    //Melee and thrown tridents get the bonus from the attack damage modifier
    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!REUtils.isArrowHit(damageSource))
            return super.modifyEnchantmentDamage(attacker, attacked, damage, originalDamage, damageSource, stack);
        return (float) (damage + (originalDamage * arrowBonusDamage));
    }
}
