package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfUndeadRune extends Rune {
    @Config(min = 2)
    public static Integer catchFireOdds = 200;
    @Config(min = 1)
    public static Integer secondsOnFire = 3;

    @Override
    public String getName() {
        return "Curse of Undead";
    }

    @Override
    public String getDescription() {
        return "May catch fire when exposed to the sun during the day, like an undead";
    }

    @Override
    public String getInfo() {
        return "Catch fire odds while exposed to sunlight: 1 in %s each second";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 20 != 0
                || !level.isDay()
                || level.isThundering()
                || entity.isInWaterRainOrBubble()
                || !level.canSeeSky(entity.blockPosition())
                || entity.getRandom().nextInt(catchFireOdds) != 0)
            return;

        entity.setRemainingFireTicks(secondsOnFire * 20);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), catchFireOdds);
    }
}
