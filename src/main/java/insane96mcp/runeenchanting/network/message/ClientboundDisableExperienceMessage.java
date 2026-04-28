package insane96mcp.runeenchanting.network.message;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneFeature;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundDisableExperienceMessage(boolean disabled) implements CustomPacketPayload {
    public static final Type<ClientboundDisableExperienceMessage> TYPE =
            new Type<>(RuneEnchanting.id("sync_experience_disabled_gamerule"));

    public static final StreamCodec<ByteBuf, ClientboundDisableExperienceMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundDisableExperienceMessage::disabled,
            ClientboundDisableExperienceMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ClientboundDisableExperienceMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            RuneFeature.disableExperience = payload.disabled;
        });
    }

    public static void sync(boolean disabled) {
        var msg = new ClientboundDisableExperienceMessage(disabled);
        PacketDistributor.sendToAllPlayers(msg);
    }

    public static void sync(ServerPlayer player, boolean disabled) {
        var msg = new ClientboundDisableExperienceMessage(disabled);
        PacketDistributor.sendToPlayer(player, msg);
    }
}