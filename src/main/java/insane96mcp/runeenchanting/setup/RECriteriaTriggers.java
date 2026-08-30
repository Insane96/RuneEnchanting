package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.criterion.CurseLearnedTrigger;
import insane96mcp.runeenchanting.data.criterion.RuneRemovedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RECriteriaTriggers {
    @SuppressWarnings("rawtypes")
    public static final DeferredRegister REGISTRY =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, RuneEnchanting.MOD_ID);

    @SuppressWarnings("unchecked")
    public static final Supplier<RuneRemovedTrigger> RUNE_REMOVED =
            REGISTRY.register("rune_removed", RuneRemovedTrigger::new);

    @SuppressWarnings("unchecked")
    public static final Supplier<CurseLearnedTrigger> CURSE_LEARNED =
            REGISTRY.register("curse_learned", CurseLearnedTrigger::new);
}
