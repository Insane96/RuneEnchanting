package insane96mcp.runeenchanting.setup;

import com.mojang.serialization.Codec;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class REAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RuneEnchanting.MOD_ID);

    ///Curses a player has learned about (by having used an item cursed with them long enough). Synced to the owning player only.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<Holder<Rune>>>> LEARNED_CURSES =
            REGISTRY.register("learned_curses", () -> AttachmentType.builder((Supplier<List<Holder<Rune>>>) ArrayList::new)
                    .serialize(RERunes.REGISTRY.holderByNameCodec().listOf())
                    .copyOnDeath()
                    .sync((holder, player) -> holder == player, ByteBufCodecs.holderRegistry(RERunes.REGISTRY_KEY).apply(ByteBufCodecs.list()))
                    .build());

    ///Cumulative durability damage dealt while wielding/wearing an item cursed with a not-yet-learned curse. Server only.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<Holder<Rune>, Integer>>> CURSE_LEARN_PROGRESS =
            REGISTRY.register("curse_learn_progress", () -> AttachmentType.builder((Supplier<Map<Holder<Rune>, Integer>>) HashMap::new)
                    .serialize(Codec.unboundedMap(RERunes.REGISTRY.holderByNameCodec(), Codec.INT))
                    .copyOnDeath()
                    .build());
}
