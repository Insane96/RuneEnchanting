package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class CurseOfTheVoid extends Rune {
    @Config(min = 0, max = 1)
    public static Double chanceToVoid = 0.35d;

    @Override
    public String getName() {
        return "Curse of the Void";
    }

    @Override
    public String getDescription() {
        return "Chance to destroy items mined / fished / dropped by mobs";
    }

    @Override
    public String getInfo() {
        return "Chance to void drops: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.VANISHING_ENCHANTABLE);
    }

    @Override
    public void onLivingDrops(LivingDropsEvent event, ItemStack stack) {
        if (event.getEntity().level().getRandom().nextFloat() >= chanceToVoid)
            return;
        event.getDrops().clear();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(chanceToVoid * 100f));
    }
}
