package insane96mcp.runeenchanting.setup;

import com.mojang.serialization.Codec;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.Rune;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class REDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<Rune>>>> RUNES =
            REGISTRY.register("runes", () -> DataComponentType.<List<Holder<Rune>>>builder()
                    .persistent(RERunes.REGISTRY.holderByNameCodec().listOf())
                    .networkSynchronized(ByteBufCodecs.holderRegistry(RERunes.REGISTRY_KEY).apply(ByteBufCodecs.list()))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SOCKETS =
            REGISTRY.register("sockets", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Rune>>> STORED_RUNE =
            REGISTRY.register("stored_rune", () -> DataComponentType.<Holder<Rune>>builder()
                    .persistent(RERunes.REGISTRY.holderByNameCodec())
                    .networkSynchronized(ByteBufCodecs.holderRegistry(RERunes.REGISTRY_KEY))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> STACKED_DAMAGE =
            REGISTRY.register("stacked_damage", () -> DataComponentType.<Float>builder()
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT)
                    .build());
}
