package insane96mcp.runeenchanting.compat;

import net.mehvahdjukaar.supplementaries.common.items.QuiverItem;
import net.mehvahdjukaar.supplementaries.common.items.components.QuiverContent;
import net.mehvahdjukaar.supplementaries.reg.ModComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Supplementaries quiver support. Only loaded through {@link SupplementariesCompat} when Supplementaries is installed.
 */
public class SupplementariesQuiver {
    /**
     * When the ammo comes from a quiver, Supplementaries returns a 1 item copy of the selected arrows from
     * {@code Player#getProjectile} and, when the weapon consumes it, the arrows are taken from the active quiver
     * (if the ammo stack isn't in the player's inventory) starting from the selected slot and moving to the
     * following non-empty slots, whatever arrows they hold. So the available arrows are the ones of the same type
     * as the ammo from the selected slot onwards, stopping at the first slot with different arrows.
     */
    public static int getAvailableAmmo(Player player, ItemStack ammo) {
        if (ammo.isEmpty() || player.getInventory().hasAnyMatching(stack -> stack == ammo))
            return ammo.getCount();
        ItemStack quiver = QuiverItem.findActiveQuiver(player);
        if (quiver.isEmpty())
            return ammo.getCount();
        QuiverContent content = quiver.get(ModComponents.QUIVER_CONTENT.get());
        if (content == null)
            return ammo.getCount();
        List<ItemStack> stacks = content.getContentUnsafe();
        int size = stacks.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            ItemStack stack = stacks.get((content.getSelectedSlot() + i) % size);
            if (stack.isEmpty())
                continue;
            if (!ItemStack.isSameItemSameComponents(stack, ammo))
                break;
            count += stack.getCount();
        }
        return Math.max(ammo.getCount(), count);
    }
}
