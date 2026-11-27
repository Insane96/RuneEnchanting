package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.CurseKnowledge;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
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
import net.minecraft.world.level.LightLayer;

public class CurseOfStaticChargeRune extends Rune {
    @Config(min = 2)
    public static Integer strikeOdds = 1000;
    @Config(min = 0, max = 15, description = "Minimum sky light at the entity's position for lightning to strike")
    public static Integer minSkyLight = 5;

    @Override
    public String getName() {
        return "Curse of Static Charge";
    }

    @Override
    public String getDescription() {
        return "May attract lightning strikes while raining, twice as likely during thunderstorms";
    }

    @Override
    public String getInfo() {
        return "Strike odds while raining: 1 in %s each second (1 in %s during thunderstorms)";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .addTag(REItemTagProvider.HORSE_ARMOR)
                .addTag(REItemTagProvider.WOLF_ARMOR);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if (!level.isRaining()
                || (entity.tickCount + entity.getId()) % 20 != 0
                || level.getBrightness(LightLayer.SKY, entity.blockPosition()) < minSkyLight)
            return;
        //Twice as likely during thunderstorms
        int odds = level.isThundering() ? strikeOdds / 2 : strikeOdds;
        if (entity.getRandom().nextInt(odds) != 0)
            return;

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
            level.addFreshEntity(bolt);
            CurseKnowledge.reveal(entity, RERunes.CURSE_OF_STATIC_CHARGE);
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), strikeOdds, strikeOdds / 2);
    }
}
