package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import insane96mcp.runeenchanting.CurseKnowledge;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//ArmorSlot is package-private
@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public class ArmorSlotMixin {
    //Reveals Curse of Binding when the player tries to take off the armor and can't
    @ModifyReturnValue(method = "mayPickup", at = @At("RETURN"))
    private boolean runeenchanting$revealBindingCurse(boolean mayPickup, Player player) {
        if (!mayPickup
                && player instanceof ServerPlayer
                && RuneHelper.hasRune(((Slot) (Object) this).getItem(), RERunes.CURSE_OF_BINDING))
            CurseKnowledge.reveal(player, RERunes.CURSE_OF_BINDING);
        return mayPickup;
    }
}
