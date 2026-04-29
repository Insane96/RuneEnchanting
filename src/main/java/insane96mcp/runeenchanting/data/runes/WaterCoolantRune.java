package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;

import javax.annotation.Nullable;

public class WaterCoolantRune extends Rune {
    public static final TagKey<EntityType<?>> SENSITIVE = TagKey.create(Registries.ENTITY_TYPE, RuneEnchanting.id("water_coolant/sensitive"));

    @Config
    public static Double bonusDamage = 0.7d;
    @Config
    public static Integer freezeAmount = 5;

    @Override
    public String getName() {
        return "Water Coolant";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt to water sensitive creatures";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public float modifyEnchantmentDamage(Player player, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!attacked.getType().is(SENSITIVE))
            return super.modifyEnchantmentDamage(player, attacked, damage, originalDamage, damageSource, stack);
        return (float) (damage + (originalDamage * bonusDamage));
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (!(attacked instanceof LivingEntity livingEntity)
                || !livingEntity.getType().is(SENSITIVE))
            return;

        livingEntity.setTicksFrozen(Entity.BASE_TICKS_REQUIRED_TO_FREEZE + 10 + (Entity.FREEZE_HURT_FREQUENCY * 2 * freezeAmount));
    }
}
