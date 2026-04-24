package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
        tag(runeApplicableTo("efficiency"))
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);

        tag(runeApplicableTo("sharpness"))
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES);
    }

    private static TagKey<Item> runeApplicableTo(String runeId) {
        return TagKey.create(Registries.ITEM, RuneEnchanting.location("rune_appliable_to/" + runeId));
    }

    @Override
    public String getName() {
        return "Rune Enchanting Item Tags";
    }
}
