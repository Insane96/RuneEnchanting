package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
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
        tag(WaterCoolantRune.SENSITIVE)
                .add(EntityType.BLAZE)
                .add(EntityType.ENDERMAN)
                .add(EntityType.GHAST)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.STRIDER)
                .add(EntityType.WITHER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOGLIN)
                .add(EntityType.ZOMBIFIED_PIGLIN);
        tag(BaneOfNosesRune.SENSITIVE)
                .add(EntityType.VILLAGER)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.WANDERING_TRADER)
                .add(EntityType.WITCH)
                .add(EntityType.PILLAGER)
                .add(EntityType.VINDICATOR)
                .add(EntityType.EVOKER)
                .add(EntityType.ILLUSIONER);
        tag(ExecutionerRune.RESISTANT)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityType.WARDEN)
                .add(EntityType.ELDER_GUARDIAN);
    }

    @Override
    public String getName() {
        return RuneEnchanting.MOD_ID + " Entity Type Tags";
    }
}
