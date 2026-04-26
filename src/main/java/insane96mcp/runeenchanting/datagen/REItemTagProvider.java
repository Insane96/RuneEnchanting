package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class REItemTagProvider extends ItemTagsProvider {

    public REItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsLookup, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var entry : RERunes.REGISTRY.entrySet()) {
            var tagKey = TagKey.create(Registries.ITEM, entry.getValue().getApplicableToItemTag());
            entry.getValue().addItemsToApplicableTag(tag(tagKey));
        }
    }

    @Override
    public String getName() {
        return "Rune Enchanting Item Tags";
    }
}
