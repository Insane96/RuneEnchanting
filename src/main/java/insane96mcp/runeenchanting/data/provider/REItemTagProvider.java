package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class REItemTagProvider extends ItemTagsProvider {

    public static final TagKey<Item> WEAPONS = TagKey.create(Registries.ITEM, RuneEnchanting.id("weapons"));

    public REItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsLookup, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(WEAPONS)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
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
