package insane96mcp.runeenchanting.network.message;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

///Sent by the Runic Table screen when the player applies, removes, inserts or extracts a rune. Everything is validated server side
public record ServerboundRunicTableActionMessage(int containerId, Action action, Holder<Rune> rune) implements CustomPacketPayload {
    public static final Type<ServerboundRunicTableActionMessage> TYPE =
            new Type<>(RuneEnchanting.id("runic_table_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRunicTableActionMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundRunicTableActionMessage::containerId,
            NeoForgeStreamCodecs.enumCodec(Action.class), ServerboundRunicTableActionMessage::action,
            ByteBufCodecs.holderRegistry(RERunes.REGISTRY_KEY), ServerboundRunicTableActionMessage::rune,
            ServerboundRunicTableActionMessage::new
    );

    public enum Action {
        ///Applies a stored rune to the tool
        APPLY,
        ///Removes a rune from the tool and puts it in the storage
        REMOVE,
        ///Takes a single stored rune as an item
        EXTRACT,
        ///Takes a stack of a stored rune as items
        EXTRACT_STACK,
        ///Puts the whole carried rune stack in the storage
        INSERT,
        ///Puts one of the carried runes in the storage
        INSERT_ONE
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ServerboundRunicTableActionMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof RunicTableMenu menu)
                    || menu.containerId != payload.containerId()
                    || !menu.stillValid(player))
                return;
            menu.handleAction(player, payload.action(), payload.rune());
        });
    }

    public static void send(int containerId, Action action, Holder<Rune> rune) {
        PacketDistributor.sendToServer(new ServerboundRunicTableActionMessage(containerId, action, rune));
    }
}
