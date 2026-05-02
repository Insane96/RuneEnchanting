package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

public class ChargedJumpRune extends Rune {
    @Config
    public static Integer timeToCharge = 8;
    @Config
    public static Integer maxJumpBoostAmplifier = 7;

    @Override
    public String getName() {
        return "Charged Jump";
    }

    @Override
    public String getDescription() {
        return "Crouch for a few seconds to charge up a strong jump";
    }

    @Override
    public @Nullable String getInfo() {
        return "Time to full charge: %ss. Max jump boost: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if (!entity.isCrouching() || !entity.onGround()) {
            stack.set(REDataComponents.CHARGED_JUMP, 0);
            return;
        }
        int ticks = stack.getOrDefault(REDataComponents.CHARGED_JUMP, 0);
        ticks++;
        if (isReadyToJump(stack)) {
            ticks = 0;
            int amplifier = 0;
            if (entity.hasEffect(MobEffects.JUMP))
                amplifier = entity.getEffect(MobEffects.JUMP).getAmplifier();
            amplifier = Math.min(amplifier + 1, maxJumpBoostAmplifier);
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, (int) (timeToCharge * 1.5d), amplifier, false, false, true));
        }
        stack.set(REDataComponents.CHARGED_JUMP, ticks);
    }

    public static boolean isReadyToJump(ItemStack stack) {
        return stack.getOrDefault(REDataComponents.CHARGED_JUMP, 0) >= timeToCharge;
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(timeToCharge * (maxJumpBoostAmplifier + 1) / 20f), Component.translatable("potion.potency." + maxJumpBoostAmplifier));
    }
}
