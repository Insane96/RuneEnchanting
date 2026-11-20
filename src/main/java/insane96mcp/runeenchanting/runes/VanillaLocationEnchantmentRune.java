package insane96mcp.runeenchanting.runes;

import insane96mcp.runeenchanting.RuneEnchanting;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Base for runes that mimic a vanilla enchantment based on location changed effects (Frost Walker, Soul Speed).
 * Instead of granting the enchantment to the item (whose effects vanilla runs only in the enchantment's slots, e.g. only the feet),
 * the enchantment's effects are run directly, so they work in any slot the rune can be applied to (e.g. horse and wolf armor).
 */
public abstract class VanillaLocationEnchantmentRune extends Rune {

    /**
     * Slots the enchantment's attribute modifiers may have been applied in (their ids are suffixed with the slot)
     */
    private static final EquipmentSlot[] SLOTS_TO_DEACTIVATE = {EquipmentSlot.FEET, EquipmentSlot.BODY};

    public abstract ResourceKey<Enchantment> getEnchantment();

    public abstract int getEnchantmentLevel();

    @Override
    public @Nullable String getInfo() {
        return "Equivalent to enchantment level %s";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), getEnchantmentLevel());
    }

    private Optional<Holder.Reference<Enchantment>> getEnchantmentHolder(LivingEntity entity) {
        return entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(getEnchantment());
    }

    private static EnchantedItemInUse itemInUse(ItemStack stack, LivingEntity entity) {
        return new EnchantedItemInUse(stack, RuneEnchanting.getEquipmentSlotForItem(stack), entity);
    }

    /**
     * Same as {@link Enchantment#runLocationChangedEffects} without the slot check
     */
    @Override
    public void onLocationChanged(ServerLevel level, ItemStack stack, LivingEntity entity) {
        getEnchantmentHolder(entity).ifPresent(holder -> {
            Enchantment enchantment = holder.value();
            int enchantmentLevel = getEnchantmentLevel();
            EnchantedItemInUse item = itemInUse(stack, entity);
            Set<EnchantmentLocationBasedEffect> set = entity.activeLocationDependentEnchantments().get(enchantment);
            for (ConditionalEffect<EnchantmentLocationBasedEffect> conditionalEffect : enchantment.getEffects(EnchantmentEffectComponents.LOCATION_CHANGED)) {
                EnchantmentLocationBasedEffect effect = conditionalEffect.effect();
                boolean active = set != null && set.contains(effect);
                if (conditionalEffect.matches(Enchantment.locationContext(level, enchantmentLevel, entity, active))) {
                    if (!active) {
                        if (set == null) {
                            set = new ObjectArraySet<>();
                            entity.activeLocationDependentEnchantments().put(enchantment, set);
                        }
                        set.add(effect);
                    }
                    effect.onChangedBlock(level, enchantmentLevel, item, entity, entity.position(), !active);
                }
                else if (set != null && set.remove(effect)) {
                    effect.onDeactivated(item, entity, entity.position(), enchantmentLevel);
                }
            }
            if (set != null && set.isEmpty())
                entity.activeLocationDependentEnchantments().remove(enchantment);
        });
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        getEnchantmentHolder(entity).ifPresent(holder -> holder.value().tick(level, getEnchantmentLevel(), itemInUse(stack, entity), entity));
    }

    @Override
    public boolean isImmuneToDamage(ServerLevel level, ItemStack stack, LivingEntity entity, DamageSource damageSource) {
        return getEnchantmentHolder(entity)
                .map(holder -> holder.value().isImmuneToDamage(level, getEnchantmentLevel(), entity, damageSource))
                .orElse(false);
    }

    @Override
    public void onEquipmentChange(LivingEquipmentChangeEvent event, ItemStack stack) {
        //The rune was active only if the item was in its slot
        if (RuneEnchanting.getEquipmentSlotForItem(stack) != event.getSlot())
            return;
        LivingEntity entity = event.getEntity();
        getEnchantmentHolder(entity).ifPresent(holder -> holder.value().stopLocationBasedEffects(getEnchantmentLevel(), new EnchantedItemInUse(stack, event.getSlot(), entity), entity));
    }

    /**
     * Fallback for when the rune stops being active without the item being unequipped (e.g. the rune is disabled).
     * Skipped if the entity wears an item with the actual enchantment, as it shares the active effects with this rune
     */
    @Override
    public void removeEffects(LivingEntity entity) {
        getEnchantmentHolder(entity).ifPresent(holder -> {
            if (!entity.activeLocationDependentEnchantments().containsKey(holder.value())
                    || EnchantmentHelper.getEnchantmentLevel(holder, entity) > 0)
                return;
            Set<EnchantmentLocationBasedEffect> set = entity.activeLocationDependentEnchantments().remove(holder.value());
            for (EnchantmentLocationBasedEffect effect : set) {
                for (EquipmentSlot slot : SLOTS_TO_DEACTIVATE) {
                    effect.onDeactivated(new EnchantedItemInUse(ItemStack.EMPTY, slot, entity), entity, entity.position(), getEnchantmentLevel());
                }
            }
        });
    }
}
