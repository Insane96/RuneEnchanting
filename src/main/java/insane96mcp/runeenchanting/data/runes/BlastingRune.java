package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class BlastingRune extends Rune {
    @Config(description = "Percentage bonus mining speed")
    public static Double bonusMiningSpeed = 2.5d;

    @Override
    public String getName() {
        return "Blasting";
    }

    @Override
    public String getDescription() {
        return "Increases mining speed the lower the explosion resistance of the block";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES);
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        if (state == null)
            return 0f;
        if (!stack.isCorrectToolForDrops(state))
            return 0f;
        if (!(stack.getItem() instanceof DiggerItem diggerItem))
            return 0f;

        return speed + (Math.max(0.04f, (7f - state.getBlock().getExplosionResistance()) * bonusMiningSpeed.floatValue()) * diggerItem.getTier().getSpeed());
    }
}
