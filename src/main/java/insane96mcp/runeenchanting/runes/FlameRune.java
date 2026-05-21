package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FlameRune extends Rune {
    @Config
    public static Double secondsOnFire = 4d;

    @Override
    public String getName() {
        return "Flame";
    }

    @Override
    public String getDescription() {
        return "Sets the entity or arrows on fire";
    }

    @Override
    public @Nullable String getInfo() {
        return "Melee attack set on fire: %ss. Ranged attack: 5s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.BOW);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER
                || !(attacked instanceof LivingEntity livingEntity))
            return;

        livingEntity.setRemainingFireTicks((int) (secondsOnFire * 20 * REUtils.getAttackStrengthScale(damageSource.getEntity())));
    }

    @Override
    public void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        arrow.setRemainingFireTicks(100 * 20);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(secondsOnFire));
    }
}
