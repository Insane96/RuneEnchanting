package insane96mcp.runeenchanting.block;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REBlockEntities;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RunicTableBlockEntity extends BlockEntity implements MenuProvider {
    private static final String STORAGE_TAG = "runes";
    private static final String RUNE_TAG = "rune";
    private static final String COUNT_TAG = "count";

    ///Rune → amount stored. Insertion order is kept so the GUI list doesn't shuffle
    private final Object2IntLinkedOpenHashMap<Holder<Rune>> storage = new Object2IntLinkedOpenHashMap<>();

    public RunicTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(REBlockEntities.RUNIC_TABLE.get(), pos, blockState);
    }

    public Object2IntMap<Holder<Rune>> getStorage() {
        return Object2IntMaps.unmodifiable(storage);
    }

    public int getCount(Holder<Rune> rune) {
        return storage.getInt(rune);
    }

    public void addRune(Holder<Rune> rune, int amount) {
        if (amount <= 0)
            return;
        storage.addTo(rune, amount);
        onStorageChanged();
    }

    ///Removes up to `amount` runes and returns how many have actually been removed
    public int removeRune(Holder<Rune> rune, int amount) {
        int stored = storage.getInt(rune);
        int removed = Math.min(stored, amount);
        if (removed <= 0)
            return 0;
        if (removed == stored)
            storage.removeInt(rune);
        else
            storage.put(rune, stored - removed);
        onStorageChanged();
        return removed;
    }

    private void onStorageChanged() {
        setChanged();
        //Sync the storage to every player that has this table's menu open
        if (level instanceof ServerLevel serverLevel)
            for (ServerPlayer player : serverLevel.players())
                if (player.containerMenu instanceof RunicTableMenu menu && menu.getBlockEntity() == this)
                    menu.syncStorage(player);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.runeenchanting.runic_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (level == null)
            return null;
        return new RunicTableMenu(containerId, playerInventory, ContainerLevelAccess.create(level, worldPosition), this);
    }

    public static ItemStack createRuneStack(Holder<Rune> rune, int count) {
        ItemStack stack = new ItemStack(REItems.RUNE.get(), count);
        stack.set(REDataComponents.STORED_RUNE.get(), rune);
        return stack;
    }

    public void dropContents(Level level, BlockPos pos) {
        for (Object2IntMap.Entry<Holder<Rune>> entry : storage.object2IntEntrySet()) {
            int remaining = entry.getIntValue();
            while (remaining > 0) {
                ItemStack stack = createRuneStack(entry.getKey(), 1);
                int count = Math.min(remaining, stack.getMaxStackSize());
                stack.setCount(count);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                remaining -= count;
            }
        }
        storage.clear();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (Object2IntMap.Entry<Holder<Rune>> entry : storage.object2IntEntrySet()) {
            Optional<ResourceLocation> id = entry.getKey().unwrapKey().map(key -> key.location());
            if (id.isEmpty())
                continue;
            CompoundTag runeTag = new CompoundTag();
            runeTag.putString(RUNE_TAG, id.get().toString());
            runeTag.putInt(COUNT_TAG, entry.getIntValue());
            list.add(runeTag);
        }
        tag.put(STORAGE_TAG, list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storage.clear();
        ListTag list = tag.getList(STORAGE_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag runeTag = list.getCompound(i);
            String idString = runeTag.getString(RUNE_TAG);
            int count = runeTag.getInt(COUNT_TAG);
            ResourceLocation id = ResourceLocation.tryParse(idString);
            Optional<Holder.Reference<Rune>> rune = id == null ? Optional.empty() : RERunes.REGISTRY.getHolder(id);
            if (rune.isEmpty()) {
                RuneEnchanting.LOGGER.warn("Runic Table at {} contained unknown rune {} (x{}), discarding it", worldPosition, idString, count);
                continue;
            }
            if (count > 0)
                storage.addTo(rune.get(), count);
        }
    }
}
