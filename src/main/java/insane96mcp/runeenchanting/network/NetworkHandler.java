package insane96mcp.runeenchanting.network;

import insane96mcp.runeenchanting.network.message.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ClientboundDisableExperienceMessage.TYPE, ClientboundDisableExperienceMessage.STREAM_CODEC, ClientboundDisableExperienceMessage::handle);
        registrar.playToClient(ClientboundEntityGlowMessage.TYPE, ClientboundEntityGlowMessage.STREAM_CODEC, ClientboundEntityGlowMessage::handle);
        registrar.playToClient(ClientboundRunicTableStorageMessage.TYPE, ClientboundRunicTableStorageMessage.STREAM_CODEC, ClientboundRunicTableStorageMessage::handle);
        registrar.playToServer(ServerboundRunicTableActionMessage.TYPE, ServerboundRunicTableActionMessage.STREAM_CODEC, ServerboundRunicTableActionMessage::handle);
        registrar.playToServer(ServerboundDoubleJumpMessage.TYPE, ServerboundDoubleJumpMessage.STREAM_CODEC, ServerboundDoubleJumpMessage::handle);
    }
}
