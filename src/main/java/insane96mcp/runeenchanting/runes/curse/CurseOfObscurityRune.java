package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
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

public class CurseOfObscurityRune extends Rune {
    @Config(min = 0, max = 1)
    public static Double chance = 0.35d;
    @Config(min = 1)
    public static Double durationSeconds = 5d;

    @Override
    public String getName() {
        return "Curse of Obscurity";
    }

    @Override
    public String getDescription() {
        return "Chance to become blind when hit";
    }

    @Override
    public String getInfo() {
        return "Chance: %s%%, Duration: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM)
            return;
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide
                || entity.getRandom().nextDouble() >= chance)
            return;
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (durationSeconds * 20)));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(chance * 100), durationSeconds);
    }
}
