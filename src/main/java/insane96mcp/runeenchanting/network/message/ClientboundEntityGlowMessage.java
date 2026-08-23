package insane96mcp.runeenchanting.network.message;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHooksClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundEntityGlowMessage(int entityId, int durationTicks) implements CustomPacketPayload {
    public static final Type<ClientboundEntityGlowMessage> TYPE =
            new Type<>(RuneEnchanting.id("entity_glow"));

    public static final StreamCodec<ByteBuf, ClientboundEntityGlowMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundEntityGlowMessage::entityId,
            ByteBufCodecs.VAR_INT, ClientboundEntityGlowMessage::durationTicks,
            ClientboundEntityGlowMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ClientboundEntityGlowMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> RuneHooksClient.addVibrationGlow(payload.entityId(), payload.durationTicks()));
    }

    public static void sync(ServerPlayer player, int entityId, int durationTicks) {
        PacketDistributor.sendToPlayer(player, new ClientboundEntityGlowMessage(entityId, durationTicks));
    }
}
