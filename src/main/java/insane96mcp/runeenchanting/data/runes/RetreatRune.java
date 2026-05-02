package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.ChatFormatting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetreatRune extends Rune {
    private static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("retreat");

    @Config
    public static Double bonusMovementSpeed = 0.5d;

    @Override
    public String getName() {
        return "Retreat";
    }

    @Override
    public String getDescription() {
        return "Gain movement speed the less health you have";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus movement speed: %.1f%%";
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 5 != 4)
            return;

        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute == null)
            return;
        float amount = getBonusSpeed(entity);
        AttributeModifier modifier = movementSpeedAttribute.getModifier(MODIFIER_ID);
        if (modifier != null && modifier.amount() == amount)
            return;

        if (modifier != null)
            movementSpeedAttribute.removeModifier(MODIFIER_ID);
        MCUtils.applyModifier(entity, Attributes.MOVEMENT_SPEED, MODIFIER_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public void addInfo(List<Component> tooltip, @Nullable Player player) {
        if (player == null)
            return;
        tooltip.add(CommonComponents.space()
                .append(Component.translatable(getInfoTranslationKey(), String.format("%.1f", getBonusSpeed(player) * 100f)))
                .withStyle(ChatFormatting.GRAY));
    }

    private float getBonusSpeed(LivingEntity entity) {
        float healthMissing = 1 - (entity.getHealth() / entity.getMaxHealth());
        return bonusMovementSpeed.floatValue() * (healthMissing * healthMissing);
    }
}
