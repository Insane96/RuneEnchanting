package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Comparator;
import java.util.Locale;

public class ClientSetup {

    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(REItems.RUNE.get(), (guiGraphics, font, stack, xOffset, yOffset) -> {
            Holder<Rune> rune = stack.get(REDataComponents.STORED_RUNE.get());
            if (rune == null)
                return false;

            String letter = rune.value().getNameComponent().getString().trim().substring(0, 1).toUpperCase(Locale.ROOT);
            int color = (Rune.isCurse(rune) ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE).getColor();

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.drawString(font, letter, xOffset + 1, yOffset + 1, color, true);
            guiGraphics.pose().popPose();
            return true;
        });
    }

    public static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(RuneApplicableItemsTooltip.class, RuneApplicableItemsTooltipComponent::new);
    }

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
