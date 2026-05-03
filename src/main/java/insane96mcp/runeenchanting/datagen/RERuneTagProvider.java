package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import insane96mcp.runeenchanting.setup.RETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class RERuneTagProvider extends TagsProvider<Rune> {

    public RERuneTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, RERunes.REGISTRY_KEY, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(RETags.CURSE)
                .add(RERunes.CURSE_OF_BINDING.getKey())
                .add(RERunes.CURSE_OF_VANISHING.getKey());
    }

    @Override
    public String getName() {
        return RuneEnchanting.MOD_ID + " Rune Tags";
    }
}
