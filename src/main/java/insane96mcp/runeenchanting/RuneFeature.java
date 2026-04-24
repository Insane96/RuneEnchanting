package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.runeenchanting.data.MiningContext;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.REItemComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@LoadFeature(canBeDisabled = false)
public class RuneFeature extends Feature {
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        MiningContext context = new MiningContext(event.getEntity(), event.getPosition(), event.getEntity().level(), event.getOriginalSpeed(), event.getNewSpeed());

    }

    @SubscribeEvent
    public void onStackAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        List<Holder<Rune>> runes = stack.get(REItemComponents.RUNES.get());
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().addAttributeModifiers(event);
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Holder<Rune>> runes = stack.get(REItemComponents.RUNES.get());
        if (runes == null)
            return;
        event.getToolTip().add(CommonComponents.space());
        for (Holder<Rune> holder : runes) {
            event.getToolTip().add(holder.value().getName());
        }
    }
}
