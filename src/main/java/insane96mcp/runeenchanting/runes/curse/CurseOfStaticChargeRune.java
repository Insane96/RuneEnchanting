package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfStaticChargeRune extends Rune {
    @Config(min = 2)
    public static Integer strikeOdds = 1000;

    @Override
    public String getName() {
        return "Curse of Static Charge";
    }

    @Override
    public String getDescription() {
        return "May attract lightning strikes during thunderstorms";
    }

    @Override
    public String getInfo() {
        return "Strike odds during thunderstorms: 1 in %s each second";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if (!level.isThundering()
                || (entity.tickCount + entity.getId()) % 20 != 0
                || entity.getRandom().nextInt(strikeOdds) != 0)
            return;

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
            level.addFreshEntity(bolt);
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), strikeOdds);
    }
}
