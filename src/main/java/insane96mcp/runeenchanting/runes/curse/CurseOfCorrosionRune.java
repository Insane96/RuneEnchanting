package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfCorrosionRune extends Rune {
    @Config
    public static Double damageAmount = 1d;
    @Config
    public static Double secondsBetweenDamage = 2d;

    @Override
    public String getName() {
        return "Curse of Corrosion";
    }

    @Override
    public String getDescription() {
        return "Take damage when touching water";
    }

    @Override
    public String getInfo() {
        return "Damage %s%% every %ss";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        int slotOffset = RuneEnchanting.getEquipmentSlotForItem(stack).getIndex();
        if ((entity.tickCount + entity.getId() + slotOffset) % (secondsBetweenDamage * 20) != 0
                || !entity.isInWaterRainOrBubble())
            return;

        entity.hurt(entity.damageSources().drown(), damageAmount.floatValue());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.ONE_DECIMAL_FORMATTER.format(damageAmount), RuneEnchanting.NO_DECIMAL_FORMATTER.format(secondsBetweenDamage));
    }
}
