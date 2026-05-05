package insane96mcp.runeenchanting.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentToRuneReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, ResourceLocation>> {
    public static final EnchantmentToRuneReloadListener INSTANCE = new EnchantmentToRuneReloadListener();

    public static Map<ResourceKey<Enchantment>, Holder<Rune>> ENCHANTMENT_TO_RUNE = new HashMap<>();

    @Override
    protected @NotNull Map<ResourceLocation, ResourceLocation> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, ResourceLocation> map = new HashMap<>();
        resourceManager.listResources("runeenchanting", path -> path.getPath().endsWith("enchantments_to_rune.json"))
                .forEach((location, resource) -> {
                    try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                        JsonObject json = GsonHelper.parse(reader);
                        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                            try {
                                map.put(ResourceLocation.parse(entry.getKey()), ResourceLocation.parse(entry.getValue().getAsString()));
                            } catch (Exception e) {
                                RuneEnchanting.LOGGER.error("Skipping invalid enchantment-to-rune entry '{}' in {}: {}", entry.getKey(), location, e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        RuneEnchanting.LOGGER.error("Failed to load enchantments_to_rune from {}: {}", location, e.getMessage());
                    }
                });
        return map;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, ResourceLocation> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ENCHANTMENT_TO_RUNE = new HashMap<>();
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : map.entrySet()) {
            Holder<Rune> runeHolder = RERunes.REGISTRY.getHolder(ResourceKey.create(RERunes.REGISTRY_KEY, entry.getValue())).orElse(null);
            if (runeHolder == null) {
                RuneEnchanting.LOGGER.warn("Unknown rune '{}' for enchantment '{}'", entry.getValue(), entry.getKey());
                continue;
            }
            ENCHANTMENT_TO_RUNE.put(ResourceKey.create(Registries.ENCHANTMENT, entry.getKey()), runeHolder);
        }
        RuneEnchanting.LOGGER.info("Loaded {} enchantment-to-rune mappings", ENCHANTMENT_TO_RUNE.size());
    }

    @Nullable
    public static Holder<Rune> getRuneForEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.unwrapKey().map(ENCHANTMENT_TO_RUNE::get).orElse(null);
    }
}
