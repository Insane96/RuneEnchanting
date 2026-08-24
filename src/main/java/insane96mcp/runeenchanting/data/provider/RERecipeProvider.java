package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.recipe.RuneReforgingRecipe;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class RERecipeProvider extends RecipeProvider {

    public RERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, REItems.ENCASED_RUNE.get())
                .requires(REItems.RUNE.get(), 2)
                .requires(Items.LAPIS_BLOCK, 5)
                .requires(Items.GOLD_BLOCK, 2)
                .unlockedBy("has_lapis_lazuli", has(Items.LAPIS_LAZULI))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        SimpleCookingRecipeBuilder.generic(
                        Ingredient.of(REItems.ENCASED_RUNE.get()),
                        RecipeCategory.MISC,
                        new ItemStack(REItems.RUNE.get()),
                        0.0F,
                        12000,
                        RERecipes.RUNE_REFORGING_SERIALIZER.get(),
                        RuneReforgingRecipe::new)
                .unlockedBy("has_encased_rune", has(REItems.ENCASED_RUNE.get()))
                .save(recipeOutput, RuneEnchanting.id("rune_reforging"));
    }
}
