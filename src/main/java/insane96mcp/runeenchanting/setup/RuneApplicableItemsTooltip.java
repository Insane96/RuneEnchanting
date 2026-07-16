package insane96mcp.runeenchanting.setup;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RuneApplicableItemsTooltip(List<ItemStack> items) implements TooltipComponent {
}
