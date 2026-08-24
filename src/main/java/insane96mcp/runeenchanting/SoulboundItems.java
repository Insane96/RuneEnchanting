package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.setup.REAttachments;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SoulboundItems {
    ///Pulls any items with the Soulbound rune out of the player's inventory and stashes them in persisted data, so they survive death instead of dropping.
    public static void stashOnDeath(Player player) {
        Inventory inventory = player.getInventory();
        List<REAttachments.SoulboundEntry> stashed = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || !RuneHelper.hasRune(stack, RERunes.SOULBOUND))
                continue;
            stashed.add(new REAttachments.SoulboundEntry(slot, stack.copy()));
            inventory.setItem(slot, ItemStack.EMPTY);
        }
        if (!stashed.isEmpty())
            player.setData(REAttachments.SOULBOUND_ITEMS, stashed);
    }

    ///Returns items stashed on death to the same inventory slot they were removed from, falling back to any free slot (or dropping them) if that slot is occupied.
    public static void restoreOnRespawn(Player player) {
        List<REAttachments.SoulboundEntry> stashed = player.getData(REAttachments.SOULBOUND_ITEMS);
        if (stashed.isEmpty())
            return;
        Inventory inventory = player.getInventory();
        for (REAttachments.SoulboundEntry entry : stashed) {
            if (entry.slot() >= 0 && entry.slot() < inventory.getContainerSize() && inventory.getItem(entry.slot()).isEmpty())
                inventory.setItem(entry.slot(), entry.stack());
            else if (!inventory.add(entry.stack()))
                player.drop(entry.stack(), false);
        }
        player.setData(REAttachments.SOULBOUND_ITEMS, new ArrayList<>());
    }
}
