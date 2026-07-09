package insane96mcp.runeenchanting.mixin.client;

import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Unique
    private static final ResourceLocation CURSE_ICON = ResourceLocation.fromNamespaceAndPath("runeenchanting", "textures/gui/curse_icon.png");

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void runeenchanting$renderCurseIcon(Font font, ItemStack stack, int x, int y, @Nullable String text, CallbackInfo ci) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack, false);
        if (runes == null
                || runes.stream().noneMatch(Rune::isCurse)
                || stack.is(REItems.RUNE))
            return;
        GuiGraphics self = (GuiGraphics) (Object) this;
        self.blit(CURSE_ICON, x, y, 200, 0.0f, 0.0f, 4, 4, 4, 4);
    }
}
