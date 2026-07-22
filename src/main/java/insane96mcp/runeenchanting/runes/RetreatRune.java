package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.Nullable;

public class RetreatRune extends Rune {
    private static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("retreat");

    @Config
    public static Double maxBonusMovementSpeed = 0.5d;

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
        return "Max bonus movement speed: %s%%";
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
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(maxBonusMovementSpeed * 100f));
    }

    @Override
    public void onEquipmentChange(LivingEquipmentChangeEvent event, ItemStack stack) {
        AttributeInstance attr = event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null)
            attr.removeModifier(MODIFIER_ID);
    }

    private float getBonusSpeed(LivingEntity entity) {
        float healthMissing = 1 - (entity.getHealth() / entity.getMaxHealth());
        return maxBonusMovementSpeed.floatValue() * (healthMissing * healthMissing);
    }
}
