package insane96mcp.runeenchanting;

import insane96mcp.insanelib.event.PlayerSprintEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RuneEnchanting.MOD_ID, value = Dist.CLIENT)
public class RuneHooksClient {
    @SubscribeEvent
    public static void onSprintCheck(PlayerSprintEvent event) {
        LocalPlayer player = event.getPlayer();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            RuneHooks.forRunes(stack, rune -> rune.onSprintCheck(event, stack));
        }
    }
}
