package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.setup.REMobEffects;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import insane96mcp.runeenchanting.util.REUtils;
import net.minecraft.core.Holder;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nullable;

public class MomentumRune extends Rune {
    @Config(description = "Maximum level of the Momentum (mining) effect, gained by breaking blocks")
    public static Integer maxMiningLevel = 32;
    @Config(description = "Maximum level of the Momentum (moving) effect, gained by walking with leggings, horse armor or wolf armor")
    public static Integer maxMovementLevel = 8;
    @Config(description = "Maximum level of the Momentum (attacking) effect, gained by attacking")
    public static Integer maxAttackLevel = 8;
    @Config(description = "Duration in ticks of the Momentum (moving) effect, refreshed every 5 blocks walked")
    public static Integer movementDuration = 100;

    @Override
    public String getName() {
        return "Momentum";
    }

    @Override
    public String getDescription() {
        return "Mine blocks to gain mining speed, walk to gain movement speed (leggings and horse/wolf armor), attack to gain attack speed";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.HOES)
                .addTag(ItemTags.SWORDS)
                .add(Items.TRIDENT)
                .addTag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .addTag(REItemTagProvider.HORSE_ARMOR)
                .addTag(REItemTagProvider.WOLF_ARMOR);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!stack.isCorrectToolForDrops(event.getState())
                || event.getState().getDestroySpeed(event.getLevel(), event.getPos()) <= 0)
            return;

        Player player = event.getPlayer();
        float destroyProgress = event.getState().getDestroyProgress(player, player.level(), event.getPos());
        int duration = destroyProgress > 0f
                ? Math.max((int) (1f / destroyProgress + 5) * 3 + 1, 20)
                : 20;
        addMomentum(player, REMobEffects.MINING_MOMENTUM, duration, maxMiningLevel);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        // Leggings, horse armor and wolf armor
        EquipmentSlot slot = RuneEnchanting.getEquipmentSlotForItem(stack);
        if ((slot != EquipmentSlot.LEGS && slot != EquipmentSlot.BODY)
                || entity.isCrouching()
                || !entity.onGround()
                || entity.walkDist % 5 >= entity.walkDistO % 5)
            return;

        addMomentum(entity, REMobEffects.MOVEMENT_MOMENTUM, movementDuration, maxMovementLevel);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || stack == null)
            return;
        if (!(damageSource.getEntity() instanceof ServerPlayer attacker))
            return;
        if (!REUtils.isAttackCharged(damageSource))
            return;

        double attackSpeed = attacker.getAttributeValue(Attributes.ATTACK_SPEED);
        int duration = Math.max((int) ((4.0 - attackSpeed) * 20), 20);
        addMomentum(attacker, REMobEffects.ATTACK_MOMENTUM, duration, maxAttackLevel);
    }

    @Override
    public @Nullable String getInfo() {
        return "Max Bonus Mining Speed: +%s%%. Max Bonus Movement Speed: +%s%%. Max Bonus Attack Speed: +%s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(),
                IAttributeExtension.FORMAT.format(maxMiningLevel * REMobEffects.MINING_MOMENTUM_PER_LEVEL * 100d),
                IAttributeExtension.FORMAT.format(maxMovementLevel * REMobEffects.MOVEMENT_MOMENTUM_PER_LEVEL * 100d),
                IAttributeExtension.FORMAT.format(maxAttackLevel * REMobEffects.ATTACK_MOMENTUM_PER_LEVEL * 100d));
    }

    /**
     * Applies the effect one level higher than the current one (capped at maxLevel) and refreshes its duration
     */
    private static void addMomentum(LivingEntity entity, Holder<MobEffect> effect, int duration, int maxLevel) {
        if (maxLevel <= 0)
            return;
        MobEffectInstance current = entity.getEffect(effect);
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, maxLevel - 1);
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
        if (amplifier == maxLevel - 1 && (current == null || current.getAmplifier() < amplifier))
            entity.playSound(RESoundEvents.MOMENTUM.get(), 0.5f, 1.75f);
    }
}
