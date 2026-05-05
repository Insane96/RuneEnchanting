package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class SteadyFallRune extends Rune {
    @Config(min = 0)
    public static Double durabilityRatio = 4d;

    @Override
    public String getName() {
        return "Steady Fall";
    }

    @Override
    public String getDescription() {
        return "Absorbs fall damage by consuming boot durability";
    }

    @Override
    public @Nullable String getInfo() {
        return "Durability cost per absorbed damage: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM)
            return;
        if (!event.getSource().is(DamageTypeTags.IS_FALL))
            return;
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel))
            return;

        int durabilityLeft = stack.getMaxDamage() - stack.getDamageValue();
        if (durabilityLeft <= 0)
            return;

        float reducedDamage = (float) Math.min(durabilityLeft / durabilityRatio, event.getNewDamage());
        if (reducedDamage <= 0)
            return;

        event.setNewDamage(event.getNewDamage() - reducedDamage);
        stack.hurtAndBreak((int) (reducedDamage * durabilityRatio), serverLevel, entity,
                item -> entity.onEquippedItemBroken(item, EquipmentSlot.FEET));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(durabilityRatio));
    }
}
