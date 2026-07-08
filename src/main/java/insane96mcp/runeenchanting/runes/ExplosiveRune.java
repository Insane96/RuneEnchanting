package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ExplosiveRune extends Rune {
    public static final ResourceLocation NBT_TIME = RuneEnchanting.id("explosive/time");
    public static final ResourceLocation NBT_POWER = RuneEnchanting.id("explosive/power");
    public static final ResourceLocation NBT_DESTROY_BLOCKS = RuneEnchanting.id("explosive/destroy_blocks");

    @Config(min = 0.1)
    public static Double explosionPower = 0.7d;
    @Config
    public static Boolean destroyBlocks = true;
    @Config(min = 0.05)
    public static Double delaySeconds = 5d;

    @Override
    public String getName() {
        return "Explosive";
    }

    @Override
    public String getDescription() {
        return "Attacks mark the target to explode after a delay";
    }

    @Override
    public @org.jetbrains.annotations.Nullable String getInfo() {
        return "Explosion power: %s, Delay: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || !(attacked instanceof LivingEntity))
            return;
        if (!REUtils.isAttackCharged(damageSource.getEntity()))
            return;
        long explodeAt = level.getGameTime() + (long) (delaySeconds * 20);
        ModNBTData.put(attacked, NBT_TIME, explodeAt);
        ModNBTData.put(attacked, NBT_POWER, explosionPower.floatValue());
        ModNBTData.put(attacked, NBT_DESTROY_BLOCKS, destroyBlocks);
    }

    public static void tick(ServerLevel level, LivingEntity entity) {
        if (!ModNBTData.contains(entity, NBT_TIME))
            return;
        long explodeAt = ModNBTData.get(entity, NBT_TIME, Long.class);
        if (level.getGameTime() < explodeAt)
            return;
        float power = ModNBTData.get(entity, NBT_POWER, Float.class);
        boolean shouldDestroyBlocks = ModNBTData.get(entity, NBT_DESTROY_BLOCKS, Boolean.class);
        ModNBTData.remove(entity, NBT_TIME);
        ModNBTData.remove(entity, NBT_POWER);
        ModNBTData.remove(entity, NBT_DESTROY_BLOCKS);
        entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(), power,
                shouldDestroyBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.MOB);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), explosionPower, delaySeconds);
    }
}
