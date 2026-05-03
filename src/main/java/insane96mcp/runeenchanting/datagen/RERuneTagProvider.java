package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class RERuneTagProvider extends TagsProvider<Rune> {
    public static final TagKey<Rune> CURSE = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("curse"));
    public static final TagKey<Rune> VANISHABLE = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("vanishable"));
    public static final TagKey<Rune> LOOT_ABANDONED_MINESHAFT = TagKey.create(RERunes.REGISTRY_KEY, loot("abandoned_mineshaft"));
    public static final TagKey<Rune> LOOT_IGLOO_CHEST = TagKey.create(RERunes.REGISTRY_KEY, loot("igloo_chest"));

    public RERuneTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, RERunes.REGISTRY_KEY, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        add(CURSE,
                RERunes.CURSE_OF_BINDING,
                RERunes.CURSE_OF_VANISHING);

        add(VANISHABLE,
                RERunes.CURSE_OF_VANISHING,
                RERunes.ENDURING);

        add(LOOT_ABANDONED_MINESHAFT,
                RERunes.EFFICIENCY,
                RERunes.BLASTING,
                RERunes.SILK_TOUCH,
                RERunes.EARTHBEND,
                RERunes.DWARFING,
                RERunes.LUCK)
                .addTag(VANISHABLE);

        add(LOOT_IGLOO_CHEST,
                RERunes.FROST_WALKER);
    }

    @Override
    public String getName() {
        return RuneEnchanting.MOD_ID + " Rune Tags";
    }

    public static ResourceLocation loot(String path) {
        return RuneEnchanting.id("loot/" + path);
    }

    @SafeVarargs
    private TagAppender<Rune> add(TagKey<Rune> tagKey, Holder<Rune>... holders) {
        var appender = tag(tagKey);
        for (var holder : holders)
            appender.add(holder.unwrapKey().orElseThrow());
        return appender;
    }
}
