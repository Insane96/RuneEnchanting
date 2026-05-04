package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class BaneOfHissingRune extends Rune {
    public static final TagKey<EntityType<?>> SENSITIVE = TagKey.create(Registries.ENTITY_TYPE, RuneEnchanting.id("bane_of_hissing/sensitive"));

    @Config
    public static Double bonusDamage = 0.7d;
    @Config
    public static Integer slownessAmplifier = 3;
    @Config(description = "In seconds")
    public static Double slownessDurationMin = 1.5d;
    @Config(description = "In seconds")
    public static Double slownessDurationMax = 3d;

    @Override
    public String getName() {
        return "Bane of Hissing";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt to hissing creatures";
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus damage: %s%%, Applies Slowness %s for %ss~%ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public float modifyEnchantmentDamage(LivingEntity attacker, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!attacked.getType().is(SENSITIVE))
            return super.modifyEnchantmentDamage(attacker, attacked, damage, originalDamage, damageSource, stack);
        return (float) (damage + (originalDamage * bonusDamage));
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || damageSource.getDirectEntity() != damageSource.getEntity()
                || !attacked.getType().is(SENSITIVE)
                || !(attacked instanceof LivingEntity livingEntity))
            return;

        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (Mth.nextDouble(level.random, slownessDurationMin, slownessDurationMax) * 20f), slownessAmplifier));
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusDamage * 100), Component.translatable("potion.potency." + slownessAmplifier), slownessDurationMin, slownessDurationMax);
    }
}
