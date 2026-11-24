package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REItems;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class REItemTagProvider extends ItemTagsProvider {

    public static final TagKey<Item> WEAPONS = TagKey.create(Registries.ITEM, RuneEnchanting.id("weapons"));
    public static final TagKey<Item> RANGED_WEAPONS = TagKey.create(Registries.ITEM, RuneEnchanting.id("ranged_weapons"));
    public static final TagKey<Item> HORSE_ARMOR = TagKey.create(Registries.ITEM, RuneEnchanting.id("horse_armor"));
    public static final TagKey<Item> WOLF_ARMOR = TagKey.create(Registries.ITEM, RuneEnchanting.id("wolf_armor"));
    /**
     * Curated set of representative items used to show which items a rune is compatible with in its tooltip.
     */
    public static final TagKey<Item> DISPLAY_ON_RUNE = TagKey.create(Registries.ITEM, RuneEnchanting.id("display_on_rune"));
    /**
     * Items that get {@code defaultSockets} sockets when data packs don't set them (e.g. other mods' equipment)
     */
    public static final TagKey<Item> DEFAULT_SOCKETS = TagKey.create(Registries.ITEM, RuneEnchanting.id("default_sockets"));
    /**
     * Items excluded from {@link #DEFAULT_SOCKETS}
     */
    public static final TagKey<Item> NO_DEFAULT_SOCKETS = TagKey.create(Registries.ITEM, RuneEnchanting.id("no_default_sockets"));

    public REItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsLookup, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(WEAPONS)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT, Items.MACE);
        tag(RANGED_WEAPONS)
                .add(Items.BOW, Items.CROSSBOW);
        tag(HORSE_ARMOR)
                .add(Items.LEATHER_HORSE_ARMOR, Items.IRON_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR, Items.DIAMOND_HORSE_ARMOR);
        tag(WOLF_ARMOR)
                .add(Items.WOLF_ARMOR);
        tag(DISPLAY_ON_RUNE)
                .add(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE)
                .add(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)
                .add(Items.SHEARS, Items.FISHING_ROD, Items.BOW, Items.CROSSBOW, Items.TRIDENT, Items.SHIELD, Items.ELYTRA)
                .add(Items.DIAMOND_HORSE_ARMOR, Items.WOLF_ARMOR);
        tag(DEFAULT_SOCKETS)
                .addTag(ItemTags.DURABILITY_ENCHANTABLE)
                .addTag(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(ItemTags.WEAPON_ENCHANTABLE)
                .addTag(ItemTags.MINING_ENCHANTABLE)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
                .addTag(ItemTags.TRIDENT_ENCHANTABLE)
                .addTag(ItemTags.FISHING_ENCHANTABLE)
                .addTag(Tags.Items.TOOLS)
                .addTag(Tags.Items.ARMORS);
        //Vanilla items that have no sockets
        tag(NO_DEFAULT_SOCKETS)
                .add(Items.SHIELD, Items.ELYTRA, Items.MACE, Items.FLINT_AND_STEEL, Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK);
        tag(ItemTags.BOOKSHELF_BOOKS).add(REItems.RUNE.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE).addTag(Tags.Items.TOOLS_SHEAR);
        tag(ItemTags.MINING_ENCHANTABLE).addTag(Tags.Items.TOOLS_SHEAR);
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
