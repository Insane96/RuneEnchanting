package insane96mcp.runeenchanting.network.message;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

///Sends the whole storage of a Runic Table to a player that has its menu open
public record ClientboundRunicTableStorageMessage(int containerId, List<Entry> entries) implements CustomPacketPayload {
    public static final Type<ClientboundRunicTableStorageMessage> TYPE =
            new Type<>(RuneEnchanting.id("runic_table_storage"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRunicTableStorageMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundRunicTableStorageMessage::containerId,
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRunicTableStorageMessage::entries,
            ClientboundRunicTableStorageMessage::new
    );

    public record Entry(Holder<Rune> rune, int count) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(RERunes.REGISTRY_KEY), Entry::rune,
                ByteBufCodecs.VAR_INT, Entry::count,
                Entry::new
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ClientboundRunicTableStorageMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof RunicTableMenu menu && menu.containerId == payload.containerId())
                menu.setClientStorage(payload.entries());
        });
    }

    public static void send(ServerPlayer player, int containerId, Object2IntMap<Holder<Rune>> storage) {
        List<Entry> entries = new ArrayList<>(storage.size());
        for (Object2IntMap.Entry<Holder<Rune>> entry : storage.object2IntEntrySet())
            entries.add(new Entry(entry.getKey(), entry.getIntValue()));
        PacketDistributor.sendToPlayer(player, new ClientboundRunicTableStorageMessage(containerId, entries));
    }
}
