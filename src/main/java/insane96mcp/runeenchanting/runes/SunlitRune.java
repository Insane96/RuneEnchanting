package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;

public class SunlitRune extends Rune {
    @Config(description = "Bonus mining speed relative to tool speed at full sunlight (Light level 12+). Also used as the bonus brushing speed")
    public static Double maxMiningSpeedMultiplier = 1.5d;

    @Override
    public String getName() {
        return "Sunlit";
    }

    @Override
    public String getDescription() {
        return "Gain mining and brushing speed under the sun";
    }

    @Override
    public @Nullable String getInfo() {
        return "Max Bonus Mining and Brushing Speed: +%s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .addTag(Tags.Items.TOOLS_SHEAR)
                .add(Items.BRUSH);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null || !stack.isCorrectToolForDrops(state))
            return speed;

        float toolSpeed = tool.rules().stream()
                .filter(r -> r.correctForDrops().orElse(false) && r.speed().isPresent())
                .map(r -> r.speed().get())
                .max(Float::compare)
                .orElse(tool.defaultMiningSpeed());

        return speed + toolSpeed * maxMiningSpeedMultiplier.floatValue() * RuneHelper.getSkyLightRatio(player, true);
    }

    @Override
    public float onBrushSpeed(Player player, ItemStack stack, float speed) {
        return speed + maxMiningSpeedMultiplier.floatValue() * RuneHelper.getSkyLightRatio(player, true);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(maxMiningSpeedMultiplier * 100));
    }
}
