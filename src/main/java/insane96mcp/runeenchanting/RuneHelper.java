package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RuneHelper {
    public static boolean hasRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES.get());
        return runes != null && runes.contains(rune);
    }

    public static boolean addRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES.get(), List.of()));
        if (runes.contains(rune))
            return false;
        if (runes.size() >= stack.getOrDefault(REDataComponents.SOCKETS, 0))
            return false;
        runes.add(rune);
        stack.set(REDataComponents.RUNES.get(), runes);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }

    public static boolean removeRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES.get(), List.of()));
        if (!runes.remove(rune))
            return false;
        if (runes.isEmpty()) {
            stack.remove(REDataComponents.RUNES.get());
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        }
        else
            stack.set(REDataComponents.RUNES.get(), runes);
        return true;
    }
}
