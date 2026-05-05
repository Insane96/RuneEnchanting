package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class AtmosphericRune extends Rune {
    private static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("atmospheric");

    @Config(description = "Bonus mining speed relative to tool speed at full sunlight (Light level 12+)")
    public static Double miningSpeedMultiplier = 1.5d;
    @Config(description = "Attack speed bonus (ADD_MULTIPLIED_BASE) at full moonlight (Light level 12+)")
    public static Double attackSpeedBonus = 0.25d;
    @Config(min = 0, max = 1, description = "Fraction of each durability damage point ignored when raining")
    public static Double rainDurabilityReduction = 0.667d;

    @Override
    public String getName() {
        return "Atmospheric";
    }

    @Override
    public String getDescription() {
        return "Gain mining speed under the sun, attack speed under the moon light, and reduced durability consumption in the rain";
    }

    @Override
    public @Nullable String getInfo() {
        return "Current Bonus Mining Speed: +%s%%. Current Bonus Attack Speed: +%s%%. Current durability loss reduction: %s%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null || !stack.isCorrectToolForDrops(state))
            return speed;

        float toolSpeed = tool.rules().stream()
                .filter(r -> r.correctForDrops().orElse(false) && r.speed().isPresent())
                .map(r -> r.speed().get())
                .max(Float::compare)
                .orElse(tool.defaultMiningSpeed());

        float sunLightRatio = getSunLightRatio(player);
        return speed + toolSpeed * miningSpeedMultiplier.floatValue() * sunLightRatio;
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 5 != 4)
            return;

        float moonLightRatio = getMoonLightRatio(entity);
        float amount = attackSpeedBonus.floatValue() * moonLightRatio;

        AttributeInstance attackSpeedAttr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttr == null)
            return;

        AttributeModifier existing = attackSpeedAttr.getModifier(MODIFIER_ID);
        if (existing != null && (float) existing.amount() == amount)
            return;

        attackSpeedAttr.removeModifier(MODIFIER_ID);
        if (amount > 0f)
            MCUtils.applyModifier(entity, Attributes.ATTACK_SPEED, MODIFIER_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public int modifyDurabilityChange(ServerLevel level, ItemStack stack, int original, int damage) {
        if (!level.isRaining() || damage <= 0)
            return damage;
        int newDamage = damage;
        for (int i = 0; i < damage; i++) {
            if (level.random.nextFloat() < rainDurabilityReduction.floatValue())
                newDamage--;
        }
        return Math.max(0, newDamage);
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack, @Nullable Player player) {
        if (player == null)
            return Component.empty();
        return Component.translatable(getInfoTranslationKey(),
                IAttributeExtension.FORMAT.format(getSunLightRatio(player) * miningSpeedMultiplier * 100),
                IAttributeExtension.FORMAT.format(getMoonLightRatio(player) * attackSpeedBonus * 100),
                IAttributeExtension.FORMAT.format((1f - rainDurabilityReduction) * 100));
    }

    private static float getSunLightRatio(Player player) {
        Level level = player.level();
        if (!level.isDay() || level.isThundering())
            return 0f;
        float sunLight = level.getBrightness(LightLayer.SKY, player.blockPosition()) - level.getSkyDarken();
        if (level.isRaining())
            sunLight *= 0.35f;
        return Math.min(sunLight, 12f) / 12f;
    }

    private static float getMoonLightRatio(LivingEntity entity) {
        Level level = entity.level();
        if (level.isDay() || level.isThundering())
            return 0f;
        float moonLight = level.getBrightness(LightLayer.SKY, entity.blockPosition());
        if (level.isRaining())
            moonLight *= 0.35f;
        return Math.min(moonLight, 12f) / 12f;
    }
}
