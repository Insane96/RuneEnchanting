package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CurseOfInefficiency extends Rune {

    @Config(min = 0, max = 1)
    public static Double miningSpeedReduction = 0.5d;

    @Override
    public String getName() {
        return "Curse of Inefficiency";
    }

    @Override
    public String getDescription() {
        return "Decreases mining speed";
    }

    @Override
    public String getInfo() {
        return "Mining speed penalty: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.MINING_ENCHANTABLE);
    }

    //Run after all the other efficiency runes
    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        return speed * 0.5f;
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(miningSpeedReduction * 100f));
    }
}
