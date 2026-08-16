package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class VibrationDetectionRune extends Rune {
    @Config(description = "Range (in blocks) within which sounds are detected")
    public static Double range = 16d;
    @Config(description = "Duration in seconds the sound source glows for")
    public static Double glowDuration = 1d;

    @Override
    public String getName() { return "Vibration Detection"; }

    @Override
    public String getDescription() { return "Living entities glow through walls for a moment when they emit a sound nearby, including yourself"; }

    @Override
    public @Nullable String getInfo() {
        return "Range: %s";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(range));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    public static void onEntitySound(ServerLevel level, Entity source) {
        if (!(source instanceof LivingEntity livingSource) || !Rune.isEnabled(RERunes.VIBRATION_DETECTION))
            return;

        boolean detected = !level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(range),
                wearer -> RuneHelper.hasRuneOnArmor(wearer, RERunes.VIBRATION_DETECTION)).isEmpty();
        if (!detected)
            return;

        livingSource.addEffect(new MobEffectInstance(MobEffects.GLOWING, (int) (glowDuration * 20d)));
    }
}
