package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ImpalingRune extends Rune {
    @Config
    public static Double bonusDamage = 0.7d;
    //@Config
    //public static Integer weaknessAmplifier = 3;
    //@Config(description = "In seconds")
    //public static Double weaknessDurationMin = 1.5d;
    //@Config(description = "In seconds")
    //public static Double weaknessDurationMax = 3d;

    @Override
    public String getName() {
        return "Impaling";
    }

    @Override
    public String getDescription() {
        return "Increases damage dealt to creatures touched by water or rain";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public float modifyEnchantmentDamage(Player player, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        if (!attacked.isInWaterOrRain())
            return super.modifyEnchantmentDamage(player, attacked, damage, originalDamage, damageSource, stack);
        return (float) (damage + (originalDamage * bonusDamage));
    }

    /*@Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || damageSource.getDirectEntity() != damageSource.getEntity()
                || !attacked.getType().is(SENSITIVE)
                || !(attacked instanceof LivingEntity livingEntity))
            return;

        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (Mth.nextDouble(level.random, weaknessDurationMin, weaknessDurationMax) * 20f), weaknessAmplifier));
    }*/
}
