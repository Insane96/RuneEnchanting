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
    /**
     * Curated set of representative items used to show which items a rune is compatible with in its tooltip.
     */
    public static final TagKey<Item> DISPLAY_ON_RUNE = TagKey.create(Registries.ITEM, RuneEnchanting.id("display_on_rune"));

    public REItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsLookup, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(WEAPONS)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
        tag(DISPLAY_ON_RUNE)
                .add(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE)
                .add(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                .add(Items.SHEARS, Items.FISHING_ROD, Items.BOW, Items.CROSSBOW, Items.TRIDENT, Items.SHIELD, Items.ELYTRA);
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
