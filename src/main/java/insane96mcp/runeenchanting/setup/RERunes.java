package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.EfficiencyRune;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.data.runes.SharpnessRune;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class RERunes {
    public static final ResourceKey<Registry<Rune>> REGISTRY_KEY = ResourceKey.createRegistryKey(RuneEnchanting.location("runes"));
    public static final Registry<Rune> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<Rune> RUNES = DeferredRegister.create(REGISTRY, RuneEnchanting.MOD_ID);
    public static final DeferredHolder<Rune, EfficiencyRune> EFFICIENCY = RUNES.register("efficiency", EfficiencyRune::new);
    public static final DeferredHolder<Rune, SharpnessRune> SHARPNESS = RUNES.register("sharpness", SharpnessRune::new);

    public static void registerRegistry(NewRegistryEvent event) {
        event.register(RERunes.REGISTRY);
    }
}
