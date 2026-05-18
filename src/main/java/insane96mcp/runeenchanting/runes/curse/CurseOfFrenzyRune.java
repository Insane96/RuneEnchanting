package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class CurseOfFrenzyRune extends Rune {
    @Config(min = 1)
    public static Double durationSeconds = 5d;

    @Override
    public String getName() {
        return "Curse of Frenzy";
    }

    @Override
    public String getDescription() {
        return "Buffs attackers with Speed, Resistance and Strength when hit";
    }

    @Override
    public String getInfo() {
        return "Buff duration: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM
                || event.getEntity().level().isClientSide
                || !(event.getSource().getEntity() instanceof LivingEntity attacker))
            return;
        int duration = (int) (durationSeconds * 20);
        attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0));
        attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 0));
        attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 0));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), durationSeconds);
    }
}
