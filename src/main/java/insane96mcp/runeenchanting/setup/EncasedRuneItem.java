package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EncasedRuneItem extends Item {
    public EncasedRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(RuneEnchanting.lang("encased_rune.tooltip")).withStyle(ChatFormatting.GRAY));
    }
}
