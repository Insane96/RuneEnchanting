package insane96mcp.runeenchanting.client;

import insane96mcp.runeenchanting.CurseKnowledge;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.block.RunicTableBlockEntity;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import insane96mcp.runeenchanting.network.message.ServerboundRunicTableActionMessage;
import insane96mcp.runeenchanting.network.message.ServerboundRunicTableActionMessage.Action;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Left: tool slot, socket counter and the tool's runes (each with a remove button).
 * Right: search box and the scrollable list of the stored runes. With a tool, only the runes that can be applied to it are listed.
 * Left click applies, right click extracts one, shift + right click extracts a stack. Clicking the list while holding runes stores them (left: all, right: one).
 * Both lists are sorted alphabetically by the shown name. Textures are placeholders drawn with fills.
 */
public class RunicTableScreen extends AbstractContainerScreen<RunicTableMenu> {
    private static final ResourceLocation TEXTURE = RuneEnchanting.id("textures/gui/container/runic_table.png");
    //GUI sprites, in textures/gui/sprites/container/runic_table/
    private static final ResourceLocation SCROLLER_SPRITE = sprite("scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = sprite("scroller_disabled");
    private static final ResourceLocation ROW_HIGHLIGHTED_SPRITE = sprite("row_highlighted");
    private static final ResourceLocation REMOVE_SPRITE = sprite("remove");
    private static final ResourceLocation REMOVE_HIGHLIGHTED_SPRITE = sprite("remove_highlighted");
    private static final ResourceLocation REMOVE_DISABLED_SPRITE = sprite("remove_disabled");
    private static final ResourceLocation INSERT_HIGHLIGHT_SPRITE = sprite("insert_highlight");

    private static ResourceLocation sprite(String name) {
        return RuneEnchanting.id("container/runic_table/" + name);
    }

    private static final int TEXT = 0xFFFFFF;
    private static final int TEXT_CURSE = 0xFF5555;
    private static final int TEXT_UNAVAILABLE = 0x777777;
    private static final int TEXT_DARK = 0x404040;
    private static final int TEXT_COUNT = 0xAAAAAA;
    private static final int TEXT_OVERLOADED = 0xAA0000;

    private static final int ROW_HEIGHT = 12;

    //Tool runes list
    private static final int TOOL_LIST_X = 8, TOOL_LIST_Y = 40, TOOL_LIST_WIDTH = 88, TOOL_LIST_ROWS = 7;
    private static final int REMOVE_BUTTON_WIDTH = 9;
    //Storage list, with the search box above
    private static final int STORAGE_X = 102, STORAGE_Y = 18, STORAGE_WIDTH = 124, STORAGE_ROWS = 9;
    private static final int SEARCH_X = STORAGE_X, SEARCH_Y = 4, SEARCH_WIDTH = STORAGE_WIDTH, SEARCH_HEIGHT = 12;
    //Scrollbar
    private static final int SCROLLBAR_X = 228, SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = STORAGE_ROWS * ROW_HEIGHT;
    private static final int SCROLLER_MIN_HEIGHT = 8;

    private int scrollOffset = 0;
    private int toolScrollOffset = 0;
    private boolean draggingScrollbar = false;
    private String searchText = "";
    private EditBox searchBox;

    ///Recomputed every frame, so clicks act on what is currently shown
    private List<Object2IntMap.Entry<Holder<Rune>>> visibleStorage = List.of();
    private List<Holder<Rune>> toolRunes = List.of();

    public RunicTableScreen(RunicTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 222;
        this.inventoryLabelX = RunicTableMenu.INVENTORY_X;
        this.inventoryLabelY = RunicTableMenu.INVENTORY_Y - 11;
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font, this.leftPos + SEARCH_X, this.topPos + SEARCH_Y, SEARCH_WIDTH, SEARCH_HEIGHT, Component.translatable("container.runeenchanting.runic_table.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setHint(Component.translatable("container.runeenchanting.runic_table.search").withStyle(ChatFormatting.DARK_GRAY));
        this.searchBox.setValue(this.searchText);
        this.searchBox.setResponder(text -> {
            this.searchText = text;
            this.scrollOffset = 0;
        });
        this.addRenderableWidget(this.searchBox);
    }

    ///Name shown for a rune: curses the player hasn't learned yet are shown as "Unknown Curse"
    private static Component getDisplayName(Holder<Rune> rune) {
        if (isUnknownCurse(rune))
            return Component.translatable(RuneEnchanting.lang("unknown_curse"));
        return rune.value().getNameComponent();
    }

    private static boolean isUnknownCurse(Holder<Rune> rune) {
        return Rune.isCurse(rune) && RuneFeature.mustLearnCurses && !CurseKnowledge.isLearned(Minecraft.getInstance().player, rune);
    }

    private static final Comparator<Holder<Rune>> ALPHABETICAL = Comparator.comparing(rune -> getDisplayName(rune).getString(), String.CASE_INSENSITIVE_ORDER);

    private void updateLists() {
        ItemStack tool = this.menu.getTool();
        String search = this.searchText.trim().toLowerCase(Locale.ROOT);
        List<Object2IntMap.Entry<Holder<Rune>>> storage = new ArrayList<>();
        for (Object2IntMap.Entry<Holder<Rune>> entry : this.menu.getStorage().object2IntEntrySet()) {
            if (!search.isEmpty() && !getDisplayName(entry.getKey()).getString().toLowerCase(Locale.ROOT).contains(search))
                continue;
            //With a tool only the runes that can be applied to it are listed
            if (!tool.isEmpty() && RuneHelper.canApplyRune(tool, entry.getKey()) != RuneHelper.ApplyCheck.OK)
                continue;
            storage.add(entry);
        }
        storage.sort(Comparator.comparing(Object2IntMap.Entry::getKey, ALPHABETICAL));
        this.visibleStorage = storage;
        //All the runes on the tool, disabled ones included so they can be removed
        List<Holder<Rune>> runes = new ArrayList<>(tool.getOrDefault(REDataComponents.RUNES, List.of()));
        runes.sort(ALPHABETICAL);
        this.toolRunes = runes;
    }

    private int getMaxScroll() {
        return Math.max(0, this.visibleStorage.size() - STORAGE_ROWS);
    }

    private int getMaxToolScroll() {
        return Math.max(0, this.toolRunes.size() - TOOL_LIST_ROWS);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateLists();
        //The lists can shrink while the screen is open
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, getMaxScroll());
        this.toolScrollOffset = Mth.clamp(this.toolScrollOffset, 0, getMaxToolScroll());
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderRuneTooltips(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        //Background, slots, list panels and scrollbar track
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderToolRunes(guiGraphics, mouseX, mouseY);
        renderStorage(guiGraphics, mouseX, mouseY);
    }

    private void renderToolRunes(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos + TOOL_LIST_X;
        int y = this.topPos + TOOL_LIST_Y;
        ItemStack tool = this.menu.getTool();
        int hoveredButton = getHoveredRemoveButton(mouseX, mouseY);
        int hoveredRow = getHoveredToolRow(mouseX, mouseY);
        for (int row = 0; row < TOOL_LIST_ROWS && row + this.toolScrollOffset < this.toolRunes.size(); row++) {
            int index = row + this.toolScrollOffset;
            Holder<Rune> rune = this.toolRunes.get(index);
            int rowY = y + row * ROW_HEIGHT;
            int buttonX = x + TOOL_LIST_WIDTH - REMOVE_BUTTON_WIDTH;
            if (hoveredRow == index)
                guiGraphics.blitSprite(ROW_HIGHLIGHTED_SPRITE, x, rowY, buttonX - x, ROW_HEIGHT);
            int color = !Rune.isEnabled(rune) ? TEXT_UNAVAILABLE : Rune.isCurse(rune) ? TEXT_CURSE : TEXT;
            String name = truncate(getDisplayName(rune).getString(), TOOL_LIST_WIDTH - REMOVE_BUTTON_WIDTH - 4);
            guiGraphics.drawString(this.font, name, x + 2, rowY + 2, color, false);
            boolean canRemove = RuneHelper.canRemoveRune(tool, rune) == RuneHelper.RemoveCheck.OK;
            ResourceLocation removeSprite = !canRemove ? REMOVE_DISABLED_SPRITE : hoveredButton == index ? REMOVE_HIGHLIGHTED_SPRITE : REMOVE_SPRITE;
            guiGraphics.blitSprite(removeSprite, buttonX, rowY, REMOVE_BUTTON_WIDTH, ROW_HEIGHT);
        }
    }

    private void renderStorage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos + STORAGE_X;
        int y = this.topPos + STORAGE_Y;
        //Holding runes over the list: the whole list is the drop target
        boolean inserting = isCarryingRune() && isOverStorage(mouseX, mouseY);
        if (inserting)
            guiGraphics.blitSprite(INSERT_HIGHLIGHT_SPRITE, x, y, STORAGE_WIDTH, STORAGE_ROWS * ROW_HEIGHT);
        else if (this.visibleStorage.isEmpty()) {
            //Nothing to show (empty storage, or everything hidden by the search or the tool)
            guiGraphics.drawString(this.font, Component.translatable("container.runeenchanting.runic_table.no_runes"), x + 4, y + 4, TEXT_COUNT, false);
            guiGraphics.drawWordWrap(this.font, Component.translatable("container.runeenchanting.runic_table.empty"), x + 4, y + 4 + ROW_HEIGHT + 2, STORAGE_WIDTH - 8, TEXT_UNAVAILABLE);
        }
        ItemStack tool = this.menu.getTool();
        int hovered = inserting ? -1 : getHoveredStorageRow(mouseX, mouseY);
        for (int row = 0; row < STORAGE_ROWS && row + this.scrollOffset < this.visibleStorage.size(); row++) {
            Object2IntMap.Entry<Holder<Rune>> entry = this.visibleStorage.get(row + this.scrollOffset);
            Holder<Rune> rune = entry.getKey();
            int rowY = y + row * ROW_HEIGHT;
            if (hovered == row + this.scrollOffset)
                guiGraphics.blitSprite(ROW_HIGHLIGHTED_SPRITE, x, rowY, STORAGE_WIDTH, ROW_HEIGHT);
            String count = "x" + entry.getIntValue();
            int countWidth = this.font.width(count);
            guiGraphics.drawString(this.font, count, x + STORAGE_WIDTH - 2 - countWidth, rowY + 2, TEXT_COUNT, false);
            //Grayed out if it can't be applied to the current tool (or, without a tool, if it's disabled)
            boolean available = tool.isEmpty() ? Rune.isEnabled(rune) : RuneHelper.canApplyRune(tool, rune) == RuneHelper.ApplyCheck.OK;
            int color = !available ? TEXT_UNAVAILABLE : Rune.isCurse(rune) ? TEXT_CURSE : TEXT;
            String name = truncate(getDisplayName(rune).getString(), STORAGE_WIDTH - countWidth - 8);
            guiGraphics.drawString(this.font, name, x + 2, rowY + 2, color, false);
        }
        renderScrollbar(guiGraphics);
    }

    private void renderScrollbar(GuiGraphics guiGraphics) {
        int x = this.leftPos + SCROLLBAR_X;
        int y = this.topPos + STORAGE_Y;
        int scrollerHeight = getScrollerHeight();
        int scrollerY = y + getScrollerOffset(scrollerHeight);
        guiGraphics.blitSprite(getMaxScroll() > 0 ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE, x, scrollerY, SCROLLBAR_WIDTH, scrollerHeight);
    }

    private int getScrollerHeight() {
        int size = this.visibleStorage.size();
        if (size <= STORAGE_ROWS)
            return SCROLLBAR_HEIGHT;
        return Math.max(SCROLLER_MIN_HEIGHT, SCROLLBAR_HEIGHT * STORAGE_ROWS / size);
    }

    private int getScrollerOffset(int scrollerHeight) {
        int maxScroll = getMaxScroll();
        if (maxScroll == 0)
            return 0;
        return (SCROLLBAR_HEIGHT - scrollerHeight) * this.scrollOffset / maxScroll;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        ItemStack tool = this.menu.getTool();
        if (!tool.isEmpty()) {
            Component sockets = Component.translatable("container.runeenchanting.runic_table.sockets", RuneHelper.countRunes(tool), RuneHelper.getSockets(tool));
            int textX = RunicTableMenu.TOOL_SLOT_X + 20;
            int textY = RunicTableMenu.TOOL_SLOT_Y + 4;
            guiGraphics.drawString(this.font, sockets, textX, textY, RuneHelper.isOverloaded(tool) ? TEXT_OVERLOADED : TEXT_DARK, false);
        }
    }

    private void renderRuneTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isCarryingRune() && isOverStorage(mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(this.font, List.of(
                    Component.translatable("container.runeenchanting.runic_table.hint.insert"),
                    Component.translatable("container.runeenchanting.runic_table.hint.insert_one")), mouseX, mouseY);
            return;
        }
        if (!this.menu.getCarried().isEmpty())
            return;
        ItemStack tool = this.menu.getTool();
        int storageRow = getHoveredStorageRow(mouseX, mouseY);
        if (storageRow >= 0) {
            //Same tooltip as the rune item (handles unknown curses, descriptions with shift and applicable items)
            Holder<Rune> rune = this.visibleStorage.get(storageRow).getKey();
            ItemStack runeStack = RunicTableBlockEntity.createRuneStack(rune, 1);
            List<Component> lines = new ArrayList<>(getTooltipFromContainerItem(runeStack));
            lines.add(CommonComponents.EMPTY);
            if (!tool.isEmpty()) {
                RuneHelper.ApplyCheck check = RuneHelper.canApplyRune(tool, rune);
                if (check == RuneHelper.ApplyCheck.OK)
                    lines.add(Component.translatable("container.runeenchanting.runic_table.hint.apply").withStyle(ChatFormatting.DARK_GRAY));
                else
                    lines.add(Component.translatable(check.getTranslationKey()).withStyle(ChatFormatting.RED));
            }
            else if (!Rune.isEnabled(rune))
                lines.add(Component.translatable(RuneHelper.ApplyCheck.DISABLED.getTranslationKey()).withStyle(ChatFormatting.RED));
            lines.add(Component.translatable("container.runeenchanting.runic_table.hint.extract").withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.translatable("container.runeenchanting.runic_table.hint.extract_stack").withStyle(ChatFormatting.DARK_GRAY));
            guiGraphics.renderTooltip(this.font, lines, runeStack.getTooltipImage(), runeStack, mouseX, mouseY);
            return;
        }
        int toolRow = getHoveredToolRow(mouseX, mouseY);
        if (toolRow < 0)
            toolRow = getHoveredRemoveButton(mouseX, mouseY);
        if (toolRow >= 0) {
            Holder<Rune> rune = this.toolRunes.get(toolRow);
            List<Component> lines = new ArrayList<>();
            boolean curse = Rune.isCurse(rune);
            lines.add(getDisplayName(rune).copy().withStyle(curse ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE));
            //Same extra infos as the item tooltip (RuneFeature.onTooltip): shown with shift held down
            TooltipFlag flag = ClientTooltipFlag.of(this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            if (!isUnknownCurse(rune)) {
                if (RuneFeature.showExtraInfos(flag)) {
                    lines.add(rune.value().getDescriptionComponent().withStyle(ChatFormatting.GRAY));
                    if (curse)
                        lines.add(Component.translatable(RuneEnchanting.lang("cursed_info")).withStyle(ChatFormatting.GRAY));
                }
                rune.value().addTooltip(tool, lines, flag);
            }
            if (!Rune.isEnabled(rune))
                lines.add(Component.translatable(RuneHelper.ApplyCheck.DISABLED.getTranslationKey()).withStyle(ChatFormatting.DARK_GRAY));
            lines.add(CommonComponents.EMPTY);
            RuneHelper.RemoveCheck check = RuneHelper.canRemoveRune(tool, rune);
            if (check == RuneHelper.RemoveCheck.OK)
                lines.add(Component.translatable("container.runeenchanting.runic_table.hint.remove").withStyle(ChatFormatting.DARK_GRAY));
            else
                lines.add(Component.translatable(check.getTranslationKey()).withStyle(ChatFormatting.RED));
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        }
    }

    private boolean isCarryingRune() {
        return RunicTableMenu.isStorableRune(this.menu.getCarried());
    }

    private boolean isOverStorage(double mouseX, double mouseY) {
        return isOver(mouseX, mouseY, STORAGE_X, STORAGE_Y, STORAGE_WIDTH, STORAGE_ROWS * ROW_HEIGHT);
    }

    private String truncate(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth)
            return text;
        return this.font.plainSubstrByWidth(text, maxWidth - this.font.width("...")) + "...";
    }

    ///Index in the visible storage list of the hovered row, or -1
    private int getHoveredStorageRow(double mouseX, double mouseY) {
        if (!isOver(mouseX, mouseY, STORAGE_X, STORAGE_Y, STORAGE_WIDTH, STORAGE_ROWS * ROW_HEIGHT))
            return -1;
        int index = (int) ((mouseY - this.topPos - STORAGE_Y) / ROW_HEIGHT) + this.scrollOffset;
        return index < this.visibleStorage.size() ? index : -1;
    }

    ///Index in the tool's runes of the hovered row (remove button excluded), or -1
    private int getHoveredToolRow(double mouseX, double mouseY) {
        if (!isOver(mouseX, mouseY, TOOL_LIST_X, TOOL_LIST_Y, TOOL_LIST_WIDTH - REMOVE_BUTTON_WIDTH, TOOL_LIST_ROWS * ROW_HEIGHT))
            return -1;
        int index = (int) ((mouseY - this.topPos - TOOL_LIST_Y) / ROW_HEIGHT) + this.toolScrollOffset;
        return index < this.toolRunes.size() ? index : -1;
    }

    ///Index in the tool's runes of the hovered remove button, or -1
    private int getHoveredRemoveButton(double mouseX, double mouseY) {
        if (!isOver(mouseX, mouseY, TOOL_LIST_X + TOOL_LIST_WIDTH - REMOVE_BUTTON_WIDTH, TOOL_LIST_Y, REMOVE_BUTTON_WIDTH, TOOL_LIST_ROWS * ROW_HEIGHT))
            return -1;
        int index = (int) ((mouseY - this.topPos - TOOL_LIST_Y) / ROW_HEIGHT) + this.toolScrollOffset;
        return index < this.toolRunes.size() ? index : -1;
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        return isOver(mouseX, mouseY, SCROLLBAR_X, STORAGE_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    private boolean isOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        double relX = mouseX - this.leftPos - x;
        double relY = mouseY - this.topPos - y;
        return relX >= 0 && relX < width && relY >= 0 && relY < height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //Clicking anywhere else unfocuses the search box
        if (!this.searchBox.isMouseOver(mouseX, mouseY))
            this.setFocused(null);
        //While holding something the list only accepts runes to store
        if (!this.menu.getCarried().isEmpty() && isOverStorage(mouseX, mouseY)) {
            if (isCarryingRune() && (button == 0 || button == 1))
                sendAction(button == 0 ? Action.INSERT : Action.INSERT_ONE, this.menu.getCarried().get(REDataComponents.STORED_RUNE));
            return true;
        }
        int storageRow = getHoveredStorageRow(mouseX, mouseY);
        if (storageRow >= 0) {
            Holder<Rune> rune = this.visibleStorage.get(storageRow).getKey();
            if (button == 0) {
                ItemStack tool = this.menu.getTool();
                if (!tool.isEmpty() && RuneHelper.canApplyRune(tool, rune) == RuneHelper.ApplyCheck.OK)
                    sendAction(Action.APPLY, rune);
                return true;
            }
            if (button == 1) {
                sendAction(hasShiftDown() ? Action.EXTRACT_STACK : Action.EXTRACT, rune);
                return true;
            }
        }
        int removeButton = getHoveredRemoveButton(mouseX, mouseY);
        if (removeButton >= 0 && button == 0) {
            Holder<Rune> rune = this.toolRunes.get(removeButton);
            if (RuneHelper.canRemoveRune(this.menu.getTool(), rune) == RuneHelper.RemoveCheck.OK)
                sendAction(Action.REMOVE, rune);
            return true;
        }
        if (button == 0 && isOverScrollbar(mouseX, mouseY)) {
            this.draggingScrollbar = true;
            scrollTo(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //While typing in the search box, keys like the inventory one must not close the screen
        if (keyCode != GLFW.GLFW_KEY_ESCAPE && this.searchBox.canConsumeInput()) {
            this.searchBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar) {
            scrollTo(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isOver(mouseX, mouseY, STORAGE_X, STORAGE_Y, SCROLLBAR_X + SCROLLBAR_WIDTH - STORAGE_X, SCROLLBAR_HEIGHT)) {
            this.scrollOffset = Mth.clamp(this.scrollOffset - (int) Math.signum(scrollY), 0, getMaxScroll());
            return true;
        }
        if (isOver(mouseX, mouseY, TOOL_LIST_X, TOOL_LIST_Y, TOOL_LIST_WIDTH, TOOL_LIST_ROWS * ROW_HEIGHT)) {
            this.toolScrollOffset = Mth.clamp(this.toolScrollOffset - (int) Math.signum(scrollY), 0, getMaxToolScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    ///Moves the scrollbar so that the scroller is centered on the mouse
    private void scrollTo(double mouseY) {
        int maxScroll = getMaxScroll();
        if (maxScroll == 0)
            return;
        int scrollerHeight = getScrollerHeight();
        double progress = (mouseY - this.topPos - STORAGE_Y - scrollerHeight / 2d) / (SCROLLBAR_HEIGHT - scrollerHeight);
        this.scrollOffset = Mth.clamp((int) Math.round(progress * maxScroll), 0, maxScroll);
    }

    private void sendAction(Action action, @Nullable Holder<Rune> rune) {
        if (rune == null)
            return;
        ServerboundRunicTableActionMessage.send(this.menu.containerId, action, rune);
        playClickSound();
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
