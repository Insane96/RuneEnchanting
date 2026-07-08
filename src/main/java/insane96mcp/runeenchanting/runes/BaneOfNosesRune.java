package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class BaneOfNosesRune extends Rune {
    public static final TagKey<EntityType<?>> SENSITIVE = TagKey.create(Registries.ENTITY_TYPE, RuneEnchanting.id("bane_of_noses/sensitive"));

    @Config
    public static Double bonusDamage = 0.7d;

    @Override
    public String getName() {
        return "Bane of Noses";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt to long nosed creatures";
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus damage: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!attacked.getType().is(SENSITIVE))
            return super.modifyEnchantmentDamage(attacker, attacked, damage, originalDamage, damageSource, stack);
        return (float) (damage + (originalDamage * bonusDamage));
    }

    /*@Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || damageSource.getDirectEntity() != damageSource.getEntity()
                || !attacked.getType().is(SENSITIVE)
                || !(attacked instanceof LivingEntity livingEntity))
            return;

        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (Mth.nextDouble(level.random, slownessDurationMin, slownessDurationMax) * 20f), slownessAmplifier));
    }*/

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDamage * 100));
    }
}
