package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class MoonlitRune extends Rune {
    //Same id as the old Atmospheric rune, whose attack speed bonus was saved on the entity, so removeEffects also removes the old modifiers
    private static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("atmospheric");

    @Config(description = "Attack speed bonus (ADD_MULTIPLIED_BASE) at full moonlight (Light level 12+)")
    public static Double maxAttackSpeedBonus = 0.25d;

    @Override
    public String getName() {
        return "Moonlit";
    }

    @Override
    public String getDescription() {
        return "Gain attack speed under the moon light";
    }

    @Override
    public @Nullable String getInfo() {
        return "Max Bonus Attack Speed: +%s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 5 != 4)
            return;

        float amount = maxAttackSpeedBonus.floatValue() * RuneHelper.getSkyLightRatio(entity, false);

        AttributeInstance attackSpeedAttr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttr == null)
            return;

        AttributeModifier existing = attackSpeedAttr.getModifier(MODIFIER_ID);
        if (existing != null && (float) existing.amount() == amount)
            return;

        attackSpeedAttr.removeModifier(MODIFIER_ID);
        if (amount > 0f)
            MCUtils.applyModifier(entity, Attributes.ATTACK_SPEED, MODIFIER_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, false);
    }

    @Override
    public void removeEffects(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null)
            attr.removeModifier(MODIFIER_ID);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(maxAttackSpeedBonus * 100));
    }
}
