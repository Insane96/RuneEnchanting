package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.List;

public class CurseOfUnstableMotionRune extends Rune {
    public static final ResourceLocation MODIFIER_ID = RuneEnchanting.id("curse_of_unstable_motion");

    private static final UniformFloat REALLY_SLOW = UniformFloat.of(-0.5f, -0.4f);
    private static final UniformFloat NEUTRAL = UniformFloat.of(-0.15f, 0.15f);
    private static final UniformFloat REALLY_FAST = UniformFloat.of(2f, 3f);
    private static final List<UniformFloat> SPEEDS = List.of(REALLY_SLOW, NEUTRAL, REALLY_FAST);

    @Config(min = 20, max = Integer.MAX_VALUE)
    public static Integer averageInterval = 200;

    @Override
    public String getName() {
        return "Curse of Unstable Motion";
    }

    @Override
    public String getDescription() {
        return "Periodically randomizes movement speed between very slow, neutral, and very fast";
    }

    @Override
    public String getInfo() {
        return "Average speed change interval: %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        RandomSource random = entity.getRandom();
        if (random.nextInt(averageInterval) != 0)
            return;

        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null)
            return;

        attr.removeModifier(MODIFIER_ID);
        double amount = SPEEDS.get(random.nextInt(SPEEDS.size())).sample(random);
        attr.addOrUpdateTransientModifier(new AttributeModifier(MODIFIER_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public void onEquipmentChange(LivingEquipmentChangeEvent event, ItemStack stack) {
        AttributeInstance attr = event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null)
            attr.removeModifier(MODIFIER_ID);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), averageInterval / 20);
    }
}
