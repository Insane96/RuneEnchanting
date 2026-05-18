package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.ExplosiveRune;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;

import javax.annotation.Nullable;

public class CurseOfExplosionRune extends Rune {
    @Config(min = 0.1)
    public static Double explosionPower = 0.5d;
    @Config
    public static Boolean destroyBlocks = false;
    @Config(min = 0.05)
    public static Double delaySeconds = 3d;

    @Override
    public String getName() {
        return "Curse of Explosion";
    }

    @Override
    public String getDescription() {
        return "Attacks cause the attacker to explode after a delay";
    }

    @Override
    public String getInfo() {
        return "Explosion power: %s, Delay: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || !(attacked instanceof LivingEntity))
            return;
        if (damageSource.getEntity() instanceof Player player && player.getAttackStrengthScale(0.5f) < 0.9f)
            return;
        if (!(damageSource.getEntity() instanceof LivingEntity attacker))
            return;
        long explodeAt = level.getGameTime() + (long) (delaySeconds * 20);
        ModNBTData.put(attacker, ExplosiveRune.NBT_TIME, explodeAt);
        ModNBTData.put(attacker, ExplosiveRune.NBT_POWER, explosionPower.floatValue());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), explosionPower, delaySeconds);
    }
}
