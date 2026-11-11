package insane96mcp.runeenchanting.menu;

import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.block.RunicTableBlockEntity;
import insane96mcp.runeenchanting.network.message.ClientboundRunicTableStorageMessage;
import insane96mcp.runeenchanting.network.message.ServerboundRunicTableActionMessage;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.*;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RunicTableMenu extends AbstractContainerMenu {
    public static final int TOOL_SLOT = 0;
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int HOTBAR_SLOT_END = INV_SLOT_END + 9;

    //Slot positions, must match the layout of RunicTableScreen
    public static final int TOOL_SLOT_X = 8, TOOL_SLOT_Y = 18;
    public static final int INVENTORY_X = 39, INVENTORY_Y = 140;

    private final ContainerLevelAccess access;
    ///Server side only
    @Nullable
    private final RunicTableBlockEntity blockEntity;
    ///Client side copy of the storage, filled by {@link ClientboundRunicTableStorageMessage}
    private final Object2IntLinkedOpenHashMap<Holder<Rune>> clientStorage = new Object2IntLinkedOpenHashMap<>();

    private final SimpleContainer toolContainer = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            RunicTableMenu.this.slotsChanged(this);
        }
    };

    //Client constructor
    public RunicTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, null);
    }

    //Server constructor
    public RunicTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, @Nullable RunicTableBlockEntity blockEntity) {
        super(REMenus.RUNIC_TABLE.get(), containerId);
        this.access = access;
        this.blockEntity = blockEntity;

        this.addSlot(new Slot(toolContainer, 0, TOOL_SLOT_X, TOOL_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isValidTool(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, INVENTORY_X + col * 18, INVENTORY_Y + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col, INVENTORY_X + col * 18, INVENTORY_Y + 58));
    }

    ///Items that can have runes applied or that already have some (so they can be removed)
    public static boolean isValidTool(ItemStack stack) {
        if (stack.isEmpty() || stack.is(REItems.RUNE) || stack.is(REItems.ENCASED_RUNE))
            return false;
        if (!stack.getOrDefault(REDataComponents.RUNES, List.of()).isEmpty())
            return true;
        return RERunes.REGISTRY.stream().anyMatch(rune -> rune.canBeAppliedTo(stack));
    }

    public static boolean isStorableRune(ItemStack stack) {
        return stack.is(REItems.RUNE) && stack.has(REDataComponents.STORED_RUNE);
    }

    @Nullable
    public RunicTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ItemStack getTool() {
        return toolContainer.getItem(0);
    }

    ///The storage of the table: the block entity's on the server, the synced copy on the client
    public Object2IntMap<Holder<Rune>> getStorage() {
        if (blockEntity != null)
            return blockEntity.getStorage();
        return Object2IntMaps.unmodifiable(clientStorage);
    }

    public void setClientStorage(List<ClientboundRunicTableStorageMessage.Entry> entries) {
        clientStorage.clear();
        for (ClientboundRunicTableStorageMessage.Entry entry : entries)
            clientStorage.put(entry.rune(), entry.count());
    }

    public void syncStorage(ServerPlayer player) {
        if (blockEntity != null)
            ClientboundRunicTableStorageMessage.send(player, containerId, blockEntity.getStorage());
    }

    ///Server side: runs an action requested by the screen. Every check is done here, the client only sends requests
    public void handleAction(ServerPlayer player, ServerboundRunicTableActionMessage.Action action, Holder<Rune> rune) {
        if (blockEntity == null)
            return;
        switch (action) {
            case APPLY -> applyRune(rune);
            case REMOVE -> removeRune(player, rune);
            case EXTRACT -> extractRune(player, rune, 1);
            case EXTRACT_STACK -> extractRune(player, rune, RunicTableBlockEntity.createRuneStack(rune, 1).getMaxStackSize());
            case INSERT -> insertCarried(rune, Integer.MAX_VALUE);
            case INSERT_ONE -> insertCarried(rune, 1);
        }
    }

    ///Puts up to amount of the runes held with the cursor in the storage
    private void insertCarried(Holder<Rune> rune, int amount) {
        ItemStack carried = getCarried();
        if (!isStorableRune(carried) || !rune.equals(carried.get(REDataComponents.STORED_RUNE)))
            return;
        int inserted = Math.min(amount, carried.getCount());
        blockEntity.addRune(rune, inserted);
        carried.shrink(inserted);
        setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
    }

    private void applyRune(Holder<Rune> rune) {
        ItemStack tool = getTool();
        if (tool.isEmpty()
                || blockEntity.getCount(rune) <= 0
                || RuneHelper.canApplyRune(tool, rune) != RuneHelper.ApplyCheck.OK)
            return;
        ItemStack result = tool.copy();
        if (!RuneHelper.addRune(result, rune))
            return;
        blockEntity.removeRune(rune, 1);
        toolContainer.setItem(0, result);
        playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
    }

    private void removeRune(ServerPlayer player, Holder<Rune> rune) {
        ItemStack tool = getTool();
        if (tool.isEmpty()
                || RuneHelper.canRemoveRune(tool, rune) != RuneHelper.RemoveCheck.OK)
            return;
        ItemStack result = tool.copy();
        if (!RuneHelper.removeRune(result, rune))
            return;
        toolContainer.setItem(0, result);
        blockEntity.addRune(rune, 1);
        RECriteriaTriggers.RUNE_REMOVED.get().trigger(player);
        playSound(SoundEvents.GRINDSTONE_USE);
    }

    private void extractRune(ServerPlayer player, Holder<Rune> rune, int amount) {
        int removed = blockEntity.removeRune(rune, amount);
        if (removed <= 0)
            return;
        //Drops what doesn't fit in the inventory
        player.getInventory().placeItemBackInInventory(RunicTableBlockEntity.createRuneStack(rune, removed));
    }

    private void playSound(SoundEvent sound) {
        access.execute((level, pos) -> level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem())
            return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == TOOL_SLOT) {
            if (!this.moveItemStackTo(stack, INV_SLOT_START, HOTBAR_SLOT_END, true))
                return ItemStack.EMPTY;
        }
        else if (isStorableRune(stack)) {
            //Shift clicked runes go straight into the storage. Server side only, the client gets the slot update
            if (blockEntity == null)
                return ItemStack.EMPTY;
            blockEntity.addRune(stack.get(REDataComponents.STORED_RUNE), stack.getCount());
            slot.setByPlayer(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        else if (isValidTool(stack) && !this.slots.get(TOOL_SLOT).hasItem()) {
            //moveItemStackTo ignores the slot's max stack size when the slot is empty, so move a single item
            this.slots.get(TOOL_SLOT).setByPlayer(stack.split(1));
        }
        else if (index < INV_SLOT_END) {
            if (!this.moveItemStackTo(stack, INV_SLOT_END, HOTBAR_SLOT_END, false))
                return ItemStack.EMPTY;
        }
        else if (!this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false))
            return ItemStack.EMPTY;

        if (stack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();
        if (stack.getCount() == original.getCount())
            return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.toolContainer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, REBlocks.RUNIC_TABLE.get());
    }
}
