package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.recipe.RuneReforgingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RERecipes {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCookingSerializer<RuneReforgingRecipe>> RUNE_REFORGING_SERIALIZER =
            REGISTRY.register("rune_reforging", () -> new SimpleCookingSerializer<>(RuneReforgingRecipe::new, 12000));
}
