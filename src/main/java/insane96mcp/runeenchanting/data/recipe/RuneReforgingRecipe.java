package insane96mcp.runeenchanting.data.recipe;

import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERecipes;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class RuneReforgingRecipe extends AbstractCookingRecipe {
    public RuneReforgingRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeType.SMELTING, group, category, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        List<? extends Holder<Rune>> allRunes = registries.lookupOrThrow(RERunes.REGISTRY_KEY).listElements().toList();
        ItemStack runeItem = RuneHelper.createRandomRuneItem(RandomSource.create(), allRunes);
        return runeItem.isEmpty() ? this.result.copy() : runeItem;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.FURNACE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RERecipes.RUNE_REFORGING_SERIALIZER.get();
    }
}
