package insane96mcp.runeenchanting.datagen;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.runes.BaneOfHissingRune;
import insane96mcp.runeenchanting.data.runes.SmiteRune;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class REEntityTypeTagProvider extends EntityTypeTagsProvider {

    public REEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BaneOfHissingRune.SENSITIVE)
                .addTag(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)
                .add(EntityType.CREEPER);
        tag(SmiteRune.SENSITIVE)
                .addTag(EntityTypeTags.SENSITIVE_TO_SMITE);
    }

    @Override
    public String getName() {
        return RuneEnchanting.MOD_ID + " Entity Type Tags";
    }
}
