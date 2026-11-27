package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.ExplosiveRune;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nullable;

public class CurseOfExplosionRune extends Rune {
    @Config(min = 0.1)
    public static Double explosionPower = 0.6d;
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
        return "Attacking or breaking blocks causes the wielder to explode after a delay. Each attack or broken block resets the delay";
    }

    @Override
    public String getInfo() {
        return "Explosion power: %s, Delay: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(REItemTagProvider.RANGED_WEAPONS)
                .addTag(ItemTags.MINING_ENCHANTABLE);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        //Instantly broken blocks (grass, flowers, ...) don't count, or the explosion could be delayed for free
        if (event.getState().getDestroySpeed(level, event.getPos()) <= 0f)
            return;
        scheduleExplosion(level, event.getPlayer());
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || !(attacked instanceof LivingEntity))
            return;
        if (!REUtils.isAttackCharged(damageSource))
            return;
        if (!(damageSource.getEntity() instanceof LivingEntity attacker))
            return;
        scheduleExplosion(level, attacker);
    }

    @Override
    public void tickTarget(ServerLevel level, LivingEntity entity) {
        //The scheduled explosion is ticked by Explosive, this takes over only when Explosive is disabled
        if (!Rune.isEnabled(RERunes.EXPLOSIVE))
            ExplosiveRune.tickScheduledExplosion(level, entity);
    }

    /// Schedules (or postpones, if already scheduled) the explosion of the wielder
    private static void scheduleExplosion(ServerLevel level, LivingEntity wielder) {
        long explodeAt = level.getGameTime() + (long) (delaySeconds * 20);
        if (!ModNBTData.contains(wielder, ExplosiveRune.NBT_TIME))
            ExplosiveRune.playPrimedSound(level, wielder);
        ModNBTData.put(wielder, ExplosiveRune.NBT_TIME, explodeAt);
        ModNBTData.put(wielder, ExplosiveRune.NBT_POWER, explosionPower.floatValue());
        ModNBTData.put(wielder, ExplosiveRune.NBT_DESTROY_BLOCKS, destroyBlocks);
        ModNBTData.put(wielder, ExplosiveRune.NBT_FROM_CURSE, true);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), explosionPower, delaySeconds);
    }
}
