package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuneHelper {
    private static boolean breakingExtraBlock = false;

    /**
     * Breaks a block as if the player mined it. Fires {@code BlockEvent.BreakEvent} so protections and claims can cancel it, while runes' {@code onBlockBreak} is skipped to avoid chain reactions
     */
    public static boolean breakExtraBlock(ServerPlayer player, BlockPos pos) {
        if (!player.level().mayInteract(player, pos))
            return false;
        breakingExtraBlock = true;
        try {
            return player.gameMode.destroyBlock(pos);
        }
        finally {
            breakingExtraBlock = false;
        }
    }

    public static boolean isBreakingExtraBlock() {
        return breakingExtraBlock;
    }

    public static boolean hasRune(ItemStack stack, Holder<Rune> rune) {
        if (!Rune.isEnabled(rune))
            return false;
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        return runes != null && runes.contains(rune) && (Rune.isCurse(rune) || !isOverloaded(stack));
    }

    public static boolean hasRuneOnArmor(LivingEntity entity, Holder<Rune> rune) {
        if (!Rune.isEnabled(rune))
            return false;
        for (ItemStack stack : entity.getArmorSlots()) {
            List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
            if (runes == null)
                continue;
            if (runes.contains(rune) && (Rune.isCurse(rune) || !isOverloaded(stack)))
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
        runes = runes.stream().filter(rune -> Rune.isEnabled(rune) && (!Rune.isCurse(rune) || !ignoreCurses)).toList();
        return runes.size();
    }

    public static int countCurses(ItemStack stack) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        if (runes == null)
            return 0;
        return (int) runes.stream().filter(rune -> Rune.isEnabled(rune) && Rune.isCurse(rune)).count();
    }

    public static boolean addRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        if (runes.contains(rune))
            return false;
        if (countRunes(stack) >= getSockets(stack) && !Rune.isCurse(rune))
            return false;
        if (Rune.isCurse(rune) && countCurses(stack) >= RuneFeature.maxCurses)
            return false;
        runes.add(rune);
        stack.set(REDataComponents.RUNES, runes);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }

    public enum ApplyCheck {
        OK, DISABLED, NOT_APPLICABLE, ALREADY_PRESENT, NO_FREE_SOCKETS, TOO_MANY_CURSES;

        public String getTranslationKey() {
            return "container.runeenchanting.runic_table.reason." + name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    public enum RemoveCheck {
        OK, NOT_PRESENT, CURSE_LOCKED, WOULD_OVERLOAD;

        public String getTranslationKey() {
            return "container.runeenchanting.runic_table.reason." + name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    ///Whether a player can apply the rune to the item (unlike {@link #addRune}, also checks if the rune is enabled and applicable)
    public static ApplyCheck canApplyRune(ItemStack stack, Holder<Rune> rune) {
        if (!Rune.isEnabled(rune))
            return ApplyCheck.DISABLED;
        if (!rune.value().canBeAppliedTo(stack))
            return ApplyCheck.NOT_APPLICABLE;
        if (stack.getOrDefault(REDataComponents.RUNES, List.of()).contains(rune))
            return ApplyCheck.ALREADY_PRESENT;
        if (Rune.isCurse(rune)) {
            if (countCurses(stack) >= RuneFeature.maxCurses)
                return ApplyCheck.TOO_MANY_CURSES;
        }
        else if (countRunes(stack) >= getSockets(stack))
            return ApplyCheck.NO_FREE_SOCKETS;
        return ApplyCheck.OK;
    }

    ///Whether a player can remove the rune from the item. Curses need extractCurses and, since they grant sockets, must not leave the item overloaded
    public static RemoveCheck canRemoveRune(ItemStack stack, Holder<Rune> rune) {
        if (!stack.getOrDefault(REDataComponents.RUNES, List.of()).contains(rune))
            return RemoveCheck.NOT_PRESENT;
        if (Rune.isCurse(rune)) {
            if (!RuneFeature.extractCurses)
                return RemoveCheck.CURSE_LOCKED;
            ItemStack result = stack.copy();
            removeRune(result, rune);
            if (isOverloaded(result))
                return RemoveCheck.WOULD_OVERLOAD;
        }
        return RemoveCheck.OK;
    }

    public static boolean removeRune(ItemStack stack, Holder<Rune> rune) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        if (!runes.remove(rune))
            return false;
        if (runes.isEmpty()) {
            stack.remove(REDataComponents.RUNES);
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        }
        else
            stack.set(REDataComponents.RUNES, runes);
        return true;
    }

    public static List<Holder<Rune>> clearRunes(ItemStack stack, boolean clearCurses) {
        List<Holder<Rune>> runes = new ArrayList<>(stack.getOrDefault(REDataComponents.RUNES, List.of()));
        List<Holder<Rune>> kept = clearCurses ? List.of() : runes.stream().filter(Rune::isCurse).toList();
        if (kept.isEmpty()) {
            //Removed instead of set to false, so items with vanilla enchantments keep their glint
            stack.remove(REDataComponents.RUNES);
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        }
        else {
            stack.set(REDataComponents.RUNES, kept);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        runes.removeAll(kept);
        return runes;
    }

    public static void addRandomRunes(ItemStack stack, int amount, RandomSource random, List<? extends Holder<Rune>> runePool) {
        if (amount <= 0) return;

        List<Holder<Rune>> eligible = runePool.stream()
                .filter(holder -> Rune.isEnabled(holder)
                        && stack.is(TagKey.create(Registries.ITEM, holder.value().getApplicableToItemTag())))
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

    /**
     * Replaces each enchanted book in the list with a random rune item. A book is kept if no rune is enabled.
     */
    public static void replaceEnchantedBooksWithRunes(List<ItemStack> items) {
        RandomSource random = RandomSource.create();
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).is(Items.ENCHANTED_BOOK))
                continue;
            ItemStack runeItem = createRandomRuneItem(random, RERunes.REGISTRY.holders().toList());
            if (!runeItem.isEmpty())
                items.set(i, runeItem);
        }
    }

    public static ItemStack createRandomRuneItem(RandomSource random, List<? extends Holder<Rune>> runePool) {
        List<Holder<Rune>> eligible = runePool.stream()
                .filter(Rune::isEnabled)
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
        return stack.getOrDefault(REDataComponents.SOCKETS, 0) + countCurses(stack);
    }

    /**
     * True if the item has more runes than sockets (e.g. the item's sockets were lowered after the runes were applied).
     * Runes on overloaded items are inactive, curses still apply.
     */
    public static boolean isOverloaded(ItemStack stack) {
        return countRunes(stack) > getSockets(stack);
    }

    //TODO Just return an empty list if no component is present
    @Nullable
    public static List<Holder<Rune>> getRunesByPriority(ItemStack stack, boolean ignoreStoredRune) {
        return getRunesByPriority(stack, ignoreStoredRune, false);
    }

    /**
     * @param includeInactive if true, runes on overloaded items are returned too (e.g. for tooltips)
     */
    @Nullable
    public static List<Holder<Rune>> getRunesByPriority(ItemStack stack, boolean ignoreStoredRune, boolean includeInactive) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES);
        if (runes == null) {
            if (ignoreStoredRune)
                return null;
            Holder<Rune> storedRune = stack.get(REDataComponents.STORED_RUNE);
            if (storedRune == null)
                return null;
            runes = List.of(storedRune);
        }
        boolean overloaded = !includeInactive && isOverloaded(stack);
        runes = runes.stream()
                .filter(Rune::isEnabled)
                .filter(rune -> !overloaded || Rune.isCurse(rune))
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

    /**
     * Items from the {@link REItemTagProvider#DISPLAY_ON_RUNE} tag that the given rune can be applied to,
     * used to show which items a rune is compatible with in its tooltip.
     */
    public static List<ItemStack> getApplicableDisplayItems(Holder<Rune> rune) {
        Optional<HolderSet.Named<Item>> displayItems = BuiltInRegistries.ITEM.getTag(REItemTagProvider.DISPLAY_ON_RUNE);
        if (displayItems.isEmpty())
            return List.of();
        return displayItems.get().stream()
                .map(Holder::value)
                .map(ItemStack::new)
                .filter(stack -> rune.value().canBeAppliedTo(stack))
                .toList();
    }

    /**
     * Sky light at the entity's position during the day (sunlight) or the night (moonlight), from 0 to 1 (Light level 12+).
     * Always 0 in dimensions with fixed time and while thundering, reduced while raining.
     */
    public static float getSkyLightRatio(LivingEntity entity, boolean day) {
        Level level = entity.level();
        if (level.dimensionType().hasFixedTime() || level.isThundering() || isDayTime(level) != day)
            return 0f;
        float skyLight = level.getBrightness(LightLayer.SKY, entity.blockPosition());
        if (level.isRaining())
            skyLight *= 0.35f;
        return Math.min(skyLight, 12f) / 12f;
    }

    /**
     * Level#isDay()/isNight()/getSkyDarken() rely on Level's internal skyDarken field, which
     * ServerLevel refreshes every tick but ClientLevel only sets once at construction time.
     * That leaves it stale client-side (e.g. always "day" if the world was joined during daytime),
     * causing mining speed to desync between the client prediction and the server's actual speed.
     * getDayTime() itself is kept live on both sides every tick, so derive day/night from it instead.
     */
    public static boolean isDayTime(Level level) {
        return level.getDayTime() % 24000L < 12000L;
    }
}
