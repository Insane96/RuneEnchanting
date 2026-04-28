package insane96mcp.runeenchanting.network;

import insane96mcp.runeenchanting.network.message.ClientboundDisableExperienceMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ClientboundDisableExperienceMessage.TYPE, ClientboundDisableExperienceMessage.STREAM_CODEC, ClientboundDisableExperienceMessage::handle);
    }
}
