package insane96mcp.runeenchanting.setup;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RuneApplicableItemsTooltipComponent implements ClientTooltipComponent {
    private static final int SLOT_SIZE = 13;
    private static final int MAX_COLUMNS = 8;
    private static final float ICON_SCALE = 0.7F;

    private final List<ItemStack> items;
    private final int columns;
    private final int rows;

    public RuneApplicableItemsTooltipComponent(RuneApplicableItemsTooltip tooltip) {
        this.items = tooltip.items();
        this.columns = Math.min(MAX_COLUMNS, this.items.size());
        this.rows = (int) Math.ceil(this.items.size() / (double) this.columns);
    }

    @Override
    public int getHeight() {
        return this.rows * SLOT_SIZE + 2;
    }

    @Override
    public int getWidth(Font font) {
        return this.columns * SLOT_SIZE;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < this.items.size(); i++) {
            int col = i % this.columns;
            int row = i / this.columns;
            int itemX = x + col * SLOT_SIZE + 1;
            int itemY = y + row * SLOT_SIZE + 1;
            ItemStack stack = this.items.get(i);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(itemX + 8.0F, itemY + 8.0F, 0.0F);
            guiGraphics.pose().scale(ICON_SCALE, ICON_SCALE, 1.0F);
            guiGraphics.pose().translate(-8.0F, -8.0F, 0.0F);
            guiGraphics.renderItem(stack, 0, 0);
            guiGraphics.renderItemDecorations(font, stack, 0, 0);
            guiGraphics.pose().popPose();
        }
    }
}
