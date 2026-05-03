package insane96mcp.runeenchanting.setup;

import com.mojang.serialization.MapCodec;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.loot.ExperienceBottleToRuneLootModifier;
import insane96mcp.runeenchanting.loot.RuneCatchAllLootModifier;
import insane96mcp.runeenchanting.loot.RuneLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RELootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RuneLootModifier>> RUNE_LOOT =
            REGISTRY.register("rune_loot", () -> RuneLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RuneCatchAllLootModifier>> RUNE_CATCH_ALL =
            REGISTRY.register("rune_catch_all", () -> RuneCatchAllLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ExperienceBottleToRuneLootModifier>> EXPERIENCE_BOTTLE_TO_RUNE =
            REGISTRY.register("experience_bottle_to_rune", () -> ExperienceBottleToRuneLootModifier.CODEC);
}
