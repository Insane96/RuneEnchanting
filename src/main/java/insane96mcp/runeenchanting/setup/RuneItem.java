package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class RuneItem extends Item {
    public RuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        Holder<Rune> rune = stack.get(REDataComponents.STORED_RUNE);
        if (rune == null)
            return Optional.empty();
        List<ItemStack> displayItems = RuneHelper.getApplicableDisplayItems(rune);
        if (displayItems.isEmpty())
            return Optional.empty();
        return Optional.of(new RuneApplicableItemsTooltip(displayItems));
    }
}
