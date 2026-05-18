package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.loot.SetRandomRuneLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RELootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> REGISTRY =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetRandomRuneLootFunction>> SET_RANDOM_RUNE =
            REGISTRY.register("set_random_rune", () -> new LootItemFunctionType<>(SetRandomRuneLootFunction.CODEC));
}
