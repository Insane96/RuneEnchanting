package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Comparator;

public class ClientSetup {

    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            for (Holder<Rune> runeHolder : RERunes.REGISTRY.holders().sorted(Comparator.comparing(Rune::isCurse).thenComparing(h -> h.getKey().location().getPath())).toList()) {
                ItemStack stack = new ItemStack(REItems.RUNE.get());
                stack.set(REDataComponents.STORED_RUNE.get(), runeHolder);
                event.accept(stack);
            }
        }
        if (RuneFeature.hideEnchantingRelatedItems) {
            event.remove(new ItemStack(Items.ENCHANTING_TABLE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.remove(new ItemStack(Items.EXPERIENCE_BOTTLE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getParentEntries().stream()
                    .filter(stack -> stack.is(Items.ENCHANTED_BOOK))
                    .toList()
                    .forEach(stack -> event.remove(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
            event.getSearchEntries().stream()
                    .filter(stack -> stack.is(Items.ENCHANTED_BOOK))
                    .toList()
                    .forEach(stack -> event.remove(stack, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY));
        }
    }
}
