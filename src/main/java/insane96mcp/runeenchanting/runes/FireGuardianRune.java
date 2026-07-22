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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

public class FireGuardianRune extends Rune {
    @Config(description = "Duration of the fire resistance effect in seconds")
    public static Double resistanceDuration = 15d;
    @Config(description = "Cooldown between activations in seconds")
    public static Double cooldown = 45d;

    @Override
    public String getName() { return "Fire Guardian"; }

    @Override
    public String getDescription() { return "Gives fire resistance for a few seconds after taking fire damage, with a cooldown"; }

    @Override
    public @Nullable String getInfo() {
        return "Fire resistance duration: %ss. Cooldown on use: %ss";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(),
                IAttributeExtension.FORMAT.format(resistanceDuration),
                IAttributeExtension.FORMAT.format(cooldown));
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
                || stack.getOrDefault(REDataComponents.FIRE_GUARDIAN_LAST_USED, 0L) + cooldown * 20d > level.getGameTime())
            return;

        event.getEntity().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, (int) (resistanceDuration * 20d)));
        stack.set(REDataComponents.FIRE_GUARDIAN_LAST_USED, level.getGameTime());
        event.getEntity().playSound(SoundEvents.FIRE_EXTINGUISH);
        event.setNewDamage(0);
    }
}
