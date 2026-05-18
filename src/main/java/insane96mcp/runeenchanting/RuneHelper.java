package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RuneHelper {
    public static boolean hasRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        return runes != null && runes.contains(rune);
    }

    public static boolean hasRuneOnArmor(LivingEntity entity, Holder<Rune> rune) {
        for (ItemStack stack : entity.getArmorSlots()) {
            List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
            if (runes == null)
                continue;
            if (runes.contains(rune))
                return true;
        }
        return false;
    }

    public static int countRunes(ItemStack stack) {
        return countRunes(stack, true);
    }

    public static int countRunes(ItemStack stack, boolean ignoreCurses) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        if (runes == null)
            return 0;
        runes = runes.stream().filter(rune -> rune.value().isEnabled() && (!Rune.isCurse(rune) || !ignoreCurses)).toList();
        return runes.size();
    }

    public static boolean addRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        if (runes.contains(rune))
            return false;
        if (countRunes(stack) >= getSockets(stack) && !Rune.isCurse(rune))
            return false;
        runes.add(rune);
        stack.set(REDataComponents.RUNES, runes);
        if (!Rune.isCurse(rune))
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        if (Rune.isCurse(rune) && getSockets(stack) > 0)
            stack.set(REDataComponents.SOCKETS, getSockets(stack) + 1);
        return true;
    }

    public static boolean removeRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        if (!runes.remove(rune))
            return false;
        if (runes.isEmpty()) {
            stack.remove(REDataComponents.RUNES);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        }
        else
            stack.set(REDataComponents.RUNES, runes);
        if (Rune.isCurse(rune) && getSockets(stack) > 0)
            stack.set(REDataComponents.SOCKETS, getSockets(stack) + 1);
        return true;
    }

    public static List<Holder<Rune>> clearRunes(ItemStack stack, boolean clearCurses) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        if (clearCurses) {
            stack.remove(REDataComponents.RUNES);
        }
        else {
            List<Holder<Rune>> cleared = new ArrayList<>(runes);
            cleared.removeIf(rune -> !Rune.isCurse(rune));
            stack.set(REDataComponents.RUNES, cleared);

            runes.removeIf(Rune::isCurse);
        }
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        return runes;
    }

    public static void addRandomRunes(ItemStack stack, int amount, RandomSource random, List<? extends Holder<Rune>> runePool) {
        if (amount <= 0) return;

        List<Holder<Rune>> eligible = runePool.stream()
                .filter(holder -> {
                    Rune rune = holder.value();
                    return rune.isEnabled()
                            && stack.is(TagKey.create(Registries.ITEM, rune.getApplicableToItemTag()));
                })
                .collect(Collectors.toCollection(ArrayList::new));

        if (eligible.isEmpty()) return;

        int freeSlots = getSockets(stack)
                - stack.getOrDefault(REDataComponents.RUNES, List.of()).size();
        if (freeSlots <= 0) return;

        int toAdd = Math.min(Math.min(amount, freeSlots), eligible.size());
        for (int i = 0; i < toAdd; i++) {
            int idx = random.nextInt(eligible.size());
            addRune(stack, eligible.get(idx));
            eligible.remove(idx);
        }
    }

    public static ItemStack createRandomRuneItem(RandomSource random, List<? extends Holder<Rune>> runePool) {
        List<Holder<Rune>> eligible = runePool.stream()
                .filter(h -> h.value().isEnabled())
                .collect(Collectors.toCollection(ArrayList::new));
        if (eligible.isEmpty())
            return ItemStack.EMPTY;
        Holder<Rune> picked = eligible.get(random.nextInt(eligible.size()));
        ItemStack runeItem = new ItemStack(REItems.RUNE);
        runeItem.set(REDataComponents.STORED_RUNE, picked);
        return runeItem;
    }

    @Nullable
    public static List<Holder<Rune>> getRunesByPriority(ItemStack stack) {
        return getRunesByPriority(stack, true);
    }

    public static int getSockets(ItemStack stack) {
        return stack.getOrDefault(REDataComponents.SOCKETS, 0);
    }

    //TODO Just return an empty list if no component is present
    @Nullable
    public static List<Holder<Rune>> getRunesByPriority(ItemStack stack, boolean ignoreStoredRune) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        if (runes == null) {
            if (ignoreStoredRune)
                return null;
            Holder<Rune> storedRune = stack.get(REDataComponents.STORED_RUNE);
            if (storedRune == null)
                return null;
            runes = List.of(storedRune);
        }
        runes = runes.stream()
                .filter(holder -> holder.value().isEnabled())
                .sorted(Comparator.comparingInt(holder -> holder.value().getPriority()))
                .toList();
        return runes;
    }

    public static boolean hasCurse(ItemStack output) {
        List<Holder<Rune>> runes = getRunesByPriority(output);
        if (runes == null)
            return false;
        return runes.stream().anyMatch(Rune::isCurse);
    }
}
