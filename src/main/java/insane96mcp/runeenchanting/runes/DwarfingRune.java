package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class DwarfingRune extends Rune {
    @Config(min = 0, description = "Percentage bonus mining speed")
    public static Double maxBonusMiningSpeed = 2.5d;

    @Override
    public String getName() {
        return "Dwarfing";
    }

    @Override
    public String getDescription() {
        return "Increases mining speed. The deeper you mine, the higher the mining speed";
    }

    @Override
    public @Nullable String getInfo() {
        return "Max bonus mining speed: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        if (state == null
                || pos == null
                || !stack.isCorrectToolForDrops(state)
                || !(stack.getItem() instanceof DiggerItem diggerItem))
            return super.onMiningSpeed(player, stack, state, pos, original, speed);

        return speed + getBonusMiningSpeed(diggerItem, player);
    }

    public static int getNormalizedY(int y, Level level) {
        int startingY = level.getSeaLevel();
        if (y > startingY)
            return -1;
        //Normalize y so 0 is the sea level
        return (y - startingY) * -1;
    }

    public static float getBonusMiningSpeed(DiggerItem diggerItem, Player player) {
        int y = getNormalizedY(player.getBlockY(), player.level());
        if (y < 0)
            return 0f;
        int maxY = player.level().getSeaLevel() - player.level().getMinBuildHeight();
        float ratio = (float) y / maxY;

        return ratio * maxBonusMiningSpeed.floatValue() * diggerItem.getTier().getSpeed();
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(maxBonusMiningSpeed));
    }
}
