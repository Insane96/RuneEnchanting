package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@LoadFeature(canBeDisabled = false)
public class RuneFeature extends Feature {
    @SubscribeEvent
    public void onStackAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        List<Holder<Rune>> runes = getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().addAttributeModifiers(event);
        }
    }

    @SubscribeEvent
    public void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
        ItemStack stack = event.getStack();
        List<Holder<Rune>> runes = getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onEnchantmentLevel(event);
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int sockets = stack.getOrDefault(REDataComponents.SOCKETS, 0);
        if (sockets > 0) {
            event.getToolTip().add(CommonComponents.space());
            event.getToolTip().add(Component.translatable("sockets").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        List<Holder<Rune>> runes = getRunesByPriority(stack);
        if (runes != null) {
            for (Holder<Rune> holder : runes) {
                event.getToolTip().add(holder.value().getNameComponent().withStyle(ChatFormatting.LIGHT_PURPLE));
                if (event.getFlags().hasShiftDown())
                    event.getToolTip().add(CommonComponents.space().append(holder.value().getDescriptionComponent()).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int onGetMaxDamage(int original, ItemStack stack) {
        List<Holder<Rune>> runes = getRunesByPriority(stack);
        if (runes == null)
            return original;
        int result = original;
        for (Holder<Rune> holder : runes) {
            result = holder.value().modifyDurability(result, stack);
        }
        return result;
    }

    @Nullable
    public static List<Holder<Rune>> getRunesByPriority(ItemStack stack) {
        List<Holder<Rune>> runes = stack.get(REDataComponents.RUNES.get());
        if (runes == null)
            return null;
        runes = runes.stream()
                .filter(holder -> holder.value().isEnabled())
                .sorted(Comparator.comparingInt(holder -> holder.value().getPriority()))
                .toList();
        return runes;
    }
}
