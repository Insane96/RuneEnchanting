package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nullable;

public class MomentumRune extends Rune {
    private static final ResourceLocation MINING_MOMENTUM_ID = RuneEnchanting.id("momentum_mining");
    private static final ResourceLocation MOVEMENT_MOMENTUM_ID = RuneEnchanting.id("momentum_movement");
    private static final ResourceLocation ATTACK_MOMENTUM_ID = RuneEnchanting.id("momentum_attack");

    private static final ResourceLocation LOC_MINING_BONUS = RuneEnchanting.id("momentum/mining_bonus");
    private static final ResourceLocation LOC_MINING_TICKS = RuneEnchanting.id("momentum/mining_ticks");
    private static final ResourceLocation LOC_MOVEMENT_BONUS = RuneEnchanting.id("momentum/move_bonus");
    private static final ResourceLocation LOC_MOVEMENT_TICKS = RuneEnchanting.id("momentum/move_ticks");
    private static final ResourceLocation LOC_ATTACK_BONUS = RuneEnchanting.id("momentum/attack_bonus");
    private static final ResourceLocation LOC_ATTACK_TICKS = RuneEnchanting.id("momentum/attack_ticks");
    private static final ResourceLocation LOC_TICK_GUARD = RuneEnchanting.id("momentum/tick_guard");

    @Config(description = "Percentage bonus mining speed added per block broken")
    public static Double miningBonusPerStack = 0.0625d;
    @Config(description = "Maximum percentage bonus mining speed")
    public static Double maxMiningBonus = 2d;
    @Config(description = "Movement speed bonus added per 5 blocks walked (ADD_MULTIPLIED_BASE)")
    public static Double movementBonusPerStack = 0.05d;
    @Config(description = "Maximum movement speed bonus")
    public static Double maxMovementBonus = 0.5d;
    @Config(description = "Attack speed bonus added per attack (ADD_VALUE)")
    public static Double attackBonusPerStack = 0.05d;
    @Config(description = "Maximum attack speed bonus")
    public static Double maxAttackBonus = 0.5d;

    @Override
    public String getName() {
        return "Momentum";
    }

    @Override
    public String getDescription() {
        return "Mine blocks to gain mining speed, walk to gain movement speed, attack to gain attack speed";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.HOES)
                .addTag(ItemTags.SWORDS)
                .add(Items.TRIDENT)
                .addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack) {
        if (!stack.isCorrectToolForDrops(event.getState())
                || event.getState().getDestroySpeed(event.getLevel(), event.getPos()) <= 0)
            return;

        Player player = event.getPlayer();
        float prevMining = ModNBTData.get(player, LOC_MINING_BONUS, Float.class);
        float bonus = Math.min(prevMining + miningBonusPerStack.floatValue(), maxMiningBonus.floatValue());
        ModNBTData.put(player, LOC_MINING_BONUS, bonus);
        if (bonus >= maxMiningBonus.floatValue() && prevMining < maxMiningBonus.floatValue())
            playMomentumMaxSound(player);

        float destroyProgress = event.getState().getDestroyProgress(player, player.level(), event.getPos());
        int duration = destroyProgress > 0f
                ? Math.max((int) (1f / destroyProgress + 5) * 3 + 1, 20)
                : 20;
        ModNBTData.put(player, LOC_MINING_TICKS, duration);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if (ModNBTData.get(entity, LOC_TICK_GUARD, Integer.class) == entity.tickCount)
            return;
        ModNBTData.put(entity, LOC_TICK_GUARD, entity.tickCount);

        Tool tool = stack.get(DataComponents.TOOL);
        float toolSpeed = tool == null ? 1f : tool.rules().stream()
                .filter(r -> r.correctForDrops().orElse(false) && r.speed().isPresent())
                .map(r -> r.speed().get())
                .max(Float::compare)
                .orElse(tool.defaultMiningSpeed());
        tickMomentum(entity, LOC_MINING_BONUS, LOC_MINING_TICKS, Attributes.MINING_EFFICIENCY, MINING_MOMENTUM_ID, AttributeModifier.Operation.ADD_VALUE, toolSpeed);

        if (!entity.isCrouching() && entity.onGround() && entity.walkDist % 5 < entity.walkDistO % 5) {
            float prevMovement = ModNBTData.get(entity, LOC_MOVEMENT_BONUS, Float.class);
            float bonus = Math.min(prevMovement + movementBonusPerStack.floatValue(), maxMovementBonus.floatValue());
            ModNBTData.put(entity, LOC_MOVEMENT_BONUS, bonus);
            ModNBTData.put(entity, LOC_MOVEMENT_TICKS, 100);
            if (bonus >= maxMovementBonus.floatValue() && prevMovement < maxMovementBonus.floatValue())
                playMomentumMaxSound(entity);
        }
        tickMomentum(entity, LOC_MOVEMENT_BONUS, LOC_MOVEMENT_TICKS, Attributes.MOVEMENT_SPEED, MOVEMENT_MOMENTUM_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 1f);

        tickMomentum(entity, LOC_ATTACK_BONUS, LOC_ATTACK_TICKS, Attributes.ATTACK_SPEED, ATTACK_MOMENTUM_ID, AttributeModifier.Operation.ADD_VALUE, 1f);
    }

    @Override
    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (target != EnchantmentTarget.ATTACKER || stack == null)
            return;
        if (!(damageSource.getEntity() instanceof ServerPlayer attacker))
            return;
        if (attacker.getAttackStrengthScale(0.5f) <= 0.9f)
            return;

        float prevAttack = ModNBTData.get(attacker, LOC_ATTACK_BONUS, Float.class);
        float bonus = Math.min(prevAttack + attackBonusPerStack.floatValue(), maxAttackBonus.floatValue());
        ModNBTData.put(attacker, LOC_ATTACK_BONUS, bonus);
        if (bonus >= maxAttackBonus.floatValue() && prevAttack < maxAttackBonus.floatValue())
            playMomentumMaxSound(attacker);

        double attackSpeed = attacker.getAttributeValue(Attributes.ATTACK_SPEED);
        int duration = Math.max((int) ((4.0 - attackSpeed) * 20), 20);
        ModNBTData.put(attacker, LOC_ATTACK_TICKS, duration);
    }

    @Override
    public @Nullable String getInfo() {
        return "Current Bonus Mining Speed: +%s. Current Bonus Movement Speed: +%s%%. Current Bonus Attack Speed: +%s";
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack, @Nullable Player player) {
        if (player == null)
            return Component.empty();
        float mining = ModNBTData.get(player, LOC_MINING_BONUS, Float.class);
        float movement = ModNBTData.get(player, LOC_MOVEMENT_BONUS, Float.class);
        float attack = ModNBTData.get(player, LOC_ATTACK_BONUS, Float.class);
        return Component.translatable(getInfoTranslationKey(),
                IAttributeExtension.FORMAT.format(mining),
                IAttributeExtension.FORMAT.format(movement * 100),
                IAttributeExtension.FORMAT.format(attack));
    }

    private static void playMomentumMaxSound(LivingEntity entity) {
        entity.playSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.75f);
    }

    private static void tickMomentum(LivingEntity entity, ResourceLocation bonusLoc, ResourceLocation ticksLoc,
                                     Holder<Attribute> attribute, ResourceLocation modifierId, AttributeModifier.Operation operation, float multiplier) {
        int ticks = ModNBTData.get(entity, ticksLoc, Integer.class);
        if (ticks > 0) {
            ModNBTData.put(entity, ticksLoc, ticks - 1);
            float bonus = ModNBTData.get(entity, bonusLoc, Float.class) * multiplier;
            AttributeInstance attr = entity.getAttribute(attribute);
            if (attr != null) {
                AttributeModifier existing = attr.getModifier(modifierId);
                if (existing == null || (float) existing.amount() != bonus) {
                    attr.removeModifier(modifierId);
                    MCUtils.applyModifier(entity, attribute, modifierId, bonus, operation);
                }
            }
        } else if (ModNBTData.contains(entity, bonusLoc)) {
            AttributeInstance attr = entity.getAttribute(attribute);
            if (attr != null)
                attr.removeModifier(modifierId);
            ModNBTData.remove(entity, bonusLoc);
            ModNBTData.remove(entity, ticksLoc);
        }
    }
}
