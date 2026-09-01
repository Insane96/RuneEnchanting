package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.BloodFueledRune;
import insane96mcp.runeenchanting.runes.ElectrocutionRune;
import insane96mcp.runeenchanting.runes.curse.CurseOfBloodPactRune;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class REDamageTypeTagProvider extends DamageTypeTagsProvider {

    public REDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(BloodFueledRune.DAMAGE_TYPE)
                .add(CurseOfBloodPactRune.DAMAGE_TYPE);
        this.tag(DamageTypeTags.NO_KNOCKBACK)
                .add(BloodFueledRune.DAMAGE_TYPE, CurseOfBloodPactRune.DAMAGE_TYPE, ElectrocutionRune.DAMAGE_TYPE);
    }

    public String getName() {
        return RuneEnchanting.MOD_ID + " Damage Type Tags";
    }
}
