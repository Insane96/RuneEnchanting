package insane96mcp.runeenchanting.data.runes;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class EarthbendRune extends Rune {
    @Config(min = 0, description = "Percentage bonus mining speed")
    public static Double bonusMiningSpeed = 0.25d;

    @Override
    public String getName() {
        return "Earthbend";
    }

    @Override
    public String getDescription() {
        return "Increases mining speed for blocks that don't require a tool";
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus mining speed: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.SHEARS);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        if (state == null
                || state.requiresCorrectToolForDrops()
                || stack.isCorrectToolForDrops(state)
                || !(stack.getItem() instanceof DiggerItem diggerItem) )
            return super.onMiningSpeed(player, stack, state, pos, original, speed);

        return speed + (bonusMiningSpeed.floatValue() * diggerItem.getTier().getSpeed());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(bonusMiningSpeed * 100));
    }
}
