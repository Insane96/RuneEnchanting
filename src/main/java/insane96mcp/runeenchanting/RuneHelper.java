package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.data.EnchantmentToRuneReloadListener;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (rune.value().isCurse() && stack.getOrDefault(REDataComponents.SOCKETS, 0) > 0)
            stack.set(REDataComponents.SOCKETS, stack.getOrDefault(REDataComponents.SOCKETS.get(), 0) + 1);
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
        if (rune.value().isCurse() && stack.getOrDefault(REDataComponents.SOCKETS, 0) > 0)
            stack.set(REDataComponents.SOCKETS, stack.getOrDefault(REDataComponents.SOCKETS.get(), 0) + 1);
        return true;
    }

    public static void clearRunes(ItemStack stack) {
        stack.remove(REDataComponents.RUNES.get());
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
    }

    public static ItemStack createRandomRuneItem(RandomSource random, Optional<HolderSet<Enchantment>> options) {
        List<Holder<Rune>> eligible = options.<List<Holder<Rune>>>map(holders -> holders.stream()
                .map(EnchantmentToRuneReloadListener::getRuneForEnchantment)
                .filter(Objects::nonNull)
                .filter(h -> h.value().isEnabled() && h.value().canGenerateRandomly())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new)))
                .orElseGet(() -> RERunes.REGISTRY.holders()
                        .filter(h -> h.value().isEnabled() && h.value().canGenerateRandomly())
                        .collect(Collectors.toCollection(ArrayList::new)));
        if (eligible.isEmpty())
            return ItemStack.EMPTY;
        Holder<Rune> picked = eligible.get(random.nextInt(eligible.size()));
        ItemStack runeItem = new ItemStack(REItems.RUNE.get());
        runeItem.set(REDataComponents.STORED_RUNE.get(), picked);
        return runeItem;
    }

    public static void addRandomRunes(ItemStack stack, int amount, RandomSource random, Optional<HolderSet<Enchantment>> options) {
        if (amount <= 0) return;

        List<Holder<Rune>> eligibleRunes = options.<List<Holder<Rune>>>map(holders -> holders.stream()
                .map(EnchantmentToRuneReloadListener::getRuneForEnchantment)
                .filter(Objects::nonNull)
                .filter(holder -> {
                    Rune rune = holder.value();
                    return rune.isEnabled() && rune.canGenerateRandomly()
                            && stack.is(TagKey.create(Registries.ITEM, rune.getApplicableToItemTag()));
                })
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new))).orElseGet(() -> RERunes.REGISTRY.holders()
                .filter(holder -> {
                    Rune rune = holder.value();
                    return rune.isEnabled() && rune.canGenerateRandomly()
                            && stack.is(TagKey.create(Registries.ITEM, rune.getApplicableToItemTag()));
                })
                .collect(Collectors.toCollection(ArrayList::new)));

        if (eligibleRunes.isEmpty()) return;

        int freeSlots = stack.getOrDefault(REDataComponents.SOCKETS, 0)
                - stack.getOrDefault(REDataComponents.RUNES.get(), List.of()).size();
        if (freeSlots <= 0) return;

        int toAdd = Math.min(amount, freeSlots);
        for (int i = 0; i < toAdd && !eligibleRunes.isEmpty(); i++) {
            int idx = random.nextInt(eligibleRunes.size());
            addRune(stack, eligibleRunes.get(idx));
            eligibleRunes.remove(idx);
        }
    }
}
