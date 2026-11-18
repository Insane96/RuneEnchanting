package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.compat.StaminaCompat;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class SprintyRune extends Rune {
    private static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("sprinty");

    @Config(min = 0d, description = "Movement speed bonus while sprinting (not while swimming), applied multiplicatively on top of the sprint speed")
    public static Double bonusSprintSpeed = 0.2d;
    @Config(min = 0d, description = "Extra hunger exhaustion while sprinting, as a percentage of the vanilla sprint exhaustion. Not applied if Stamina is installed")
    public static Double extraExhaustion = 0.5d;
    @Config(min = 0d, description = "Extra stamina consumed while sprinting, as a percentage of the normal consumption. Only applied if Stamina is installed")
    public static Double extraStaminaConsumption = 0.3d;

    @Override
    public String getName() {
        return "Sprinty";
    }

    @Override
    public String getDescription() {
        return "Faster sprinting, but sprinting is more tiring";
    }

    @Override
    public @Nullable String getInfo() {
        return "Sprint speed: +%s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusSprintSpeed * 100f));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute == null)
            return;

        AttributeModifier modifier = movementSpeedAttribute.getModifier(MODIFIER_ID);
        if (!isSprintingOnLand(entity)) {
            if (modifier != null)
                movementSpeedAttribute.removeModifier(MODIFIER_ID);
            return;
        }
        if (modifier != null && modifier.amount() == bonusSprintSpeed)
            return;

        if (modifier != null)
            movementSpeedAttribute.removeModifier(MODIFIER_ID);
        MCUtils.applyModifier(entity, Attributes.MOVEMENT_SPEED, MODIFIER_ID, bonusSprintSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }

    @Override
    public void removeEffects(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null)
            attr.removeModifier(MODIFIER_ID);
    }

    /**
     * Sprinting where the bonus speed actually applies: not swimming, in water, gliding, flying or riding
     */
    public static boolean isSprintingOnLand(LivingEntity entity) {
        if (!entity.isSprinting()
                || entity.isSwimming()
                || entity.isInWater()
                || entity.isFallFlying()
                || entity.isPassenger())
            return false;
        return !(entity instanceof Player player) || !player.getAbilities().flying;
    }

    /**
     * Called from the vanilla sprint exhaustion in {@code ServerPlayer#checkMovementStatistics} (only fires when
     * sprinting on ground, not in water)
     */
    public static float modifySprintExhaustion(Player player, float exhaustion) {
        if (StaminaCompat.isLoaded() || !RuneHelper.hasRuneOnArmor(player, RERunes.SPRINTY))
            return exhaustion;
        return exhaustion * (1f + extraExhaustion.floatValue());
    }

    /**
     * Called from Stamina's stamina consumed event, only if Stamina is installed
     */
    public static float modifyStaminaConsumed(Player player, float amount) {
        if (!isSprintingOnLand(player) || !RuneHelper.hasRuneOnArmor(player, RERunes.SPRINTY))
            return amount;
        return amount * (1f + extraStaminaConsumption.floatValue());
    }
}
