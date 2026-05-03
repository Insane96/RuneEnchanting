package insane96mcp.runeenchanting.network.message;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.data.runes.DoubleJumpRune;
import insane96mcp.runeenchanting.setup.RERunes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundDoubleJumpMessage() implements CustomPacketPayload {
    public static final Type<ServerboundDoubleJumpMessage> TYPE =
            new Type<>(RuneEnchanting.id("double_jump"));

    public static final StreamCodec<ByteBuf, ServerboundDoubleJumpMessage> STREAM_CODEC =
            StreamCodec.unit(new ServerboundDoubleJumpMessage());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ServerboundDoubleJumpMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (!RuneHelper.hasRune(boots, RERunes.DOUBLE_JUMP))
                return;
            int damage = (int) Math.ceil(DoubleJumpRune.durabilityConsumed * boots.getMaxDamage());
            if (damage <= 0)
                return;
            boots.hurtAndBreak(damage, (ServerLevel) player.level(), player,
                    item -> player.onEquippedItemBroken(item, EquipmentSlot.FEET));
        });
    }

    public static void send() {
        PacketDistributor.sendToServer(new ServerboundDoubleJumpMessage());
    }
}