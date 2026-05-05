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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class BlastingRune extends Rune {
    @Config(min = 0, description = "Percentage bonus mining speed")
    public static Double bonusMiningSpeed = 2.5d;

    @Override
    public String getName() {
        return "Blasting";
    }

    @Override
    public String getDescription() {
        return "Increases mining speed. The lower the explosion resistance of the block, the higher the mining speed";
    }

    @Override
    public @javax.annotation.Nullable String getInfo() {
        return "Up to %s%% bonus mining speed";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        if (state == null
                || !stack.isCorrectToolForDrops(state)
                || !(stack.getItem() instanceof DiggerItem diggerItem))
            return super.onMiningSpeed(player, stack, state, pos, original, speed);

        //noinspection deprecation
        return speed + (Math.max(0.20f, (6f - state.getBlock().getExplosionResistance()) * bonusMiningSpeed.floatValue()) * diggerItem.getTier().getSpeed());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusMiningSpeed * 100));
    }
}
