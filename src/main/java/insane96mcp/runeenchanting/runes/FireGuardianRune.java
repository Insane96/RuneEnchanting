package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class FireGuardianRune extends Rune {
    @Config(description = "Duration of the fire resistance effect in ticks")
    public static Integer fireDuration = 300;
    @Config(description = "Cooldown between activations in ticks")
    public static Integer cooldown = 600;

    @Override
    public String getName() { return "Fire Guardian"; }

    @Override
    public String getDescription() { return "Gives fire resistance for a few seconds after taking fire damage, with a cooldown"; }

    @Override
    public @Nullable String getInfo() {
        return "Cooldown: %s. Fire resistance: %ss. Cooldown on use: %ss";
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack, @Nullable Player player) {
        String cooldownText;
        if (player == null) {
            cooldownText = IAttributeExtension.FORMAT.format(cooldown / 20.0) + "s";
        } else {
            long lastUsed = stack.getOrDefault(REDataComponents.FIRE_GUARDIAN_LAST_USED, 0L);
            long remaining = lastUsed + cooldown - player.level().getGameTime();
            cooldownText = remaining <= 0 ? "Ready" : IAttributeExtension.FORMAT.format(remaining / 20.0) + "s";
        }
        return Component.translatable(getInfoTranslationKey(),
                cooldownText,
                IAttributeExtension.FORMAT.format(fireDuration / 20.0),
                IAttributeExtension.FORMAT.format(cooldown / 20.0));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM
                || !(event.getEntity().level() instanceof ServerLevel level)
                || !event.getSource().is(DamageTypeTags.IS_FIRE)
                || event.getEntity().getEffect(MobEffects.FIRE_RESISTANCE) != null
                || stack.getOrDefault(REDataComponents.FIRE_GUARDIAN_LAST_USED, 0L) + cooldown > level.getGameTime())
            return;

        event.getEntity().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireDuration));
        stack.set(REDataComponents.FIRE_GUARDIAN_LAST_USED, level.getGameTime());
        event.getEntity().playSound(SoundEvents.FIRE_EXTINGUISH);
        event.setNewDamage(0);
    }
}
