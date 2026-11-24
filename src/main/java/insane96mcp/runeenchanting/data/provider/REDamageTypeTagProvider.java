package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.BloodFueledRune;
import insane96mcp.runeenchanting.runes.ElectrocutionRune;
import insane96mcp.runeenchanting.runes.curse.CurseOfBloodPactRune;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
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
        //Otherwise the damage would consume armor durability, which could trigger Blood Pact again
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(BloodFueledRune.DAMAGE_TYPE)
                .add(CurseOfBloodPactRune.DAMAGE_TYPE);
        this.tag(DamageTypeTags.NO_KNOCKBACK)
                .add(BloodFueledRune.DAMAGE_TYPE, CurseOfBloodPactRune.DAMAGE_TYPE, ElectrocutionRune.DAMAGE_TYPE);

        //Damage types protected by protection runes (Protection uses a blacklist instead)
        this.tag(RERunes.PROTECTION.get().getDamageTypeTag())
                .addTags(Tags.DamageTypes.IS_POISON, Tags.DamageTypes.IS_WITHER);
        this.tag(RERunes.FIRE_PROTECTION.get().getDamageTypeTag())
                .addTag(DamageTypeTags.IS_FIRE);
        this.tag(RERunes.BLAST_PROTECTION.get().getDamageTypeTag())
                .addTag(DamageTypeTags.IS_EXPLOSION);
        this.tag(RERunes.PROJECTILE_PROTECTION.get().getDamageTypeTag())
                .addTag(DamageTypeTags.IS_PROJECTILE);
        this.tag(RERunes.FEATHER_FALLING.get().getDamageTypeTag())
                .addTag(DamageTypeTags.IS_FALL);
        this.tag(RERunes.MAGIC_PROTECTION.get().getDamageTypeTag())
                .addTag(Tags.DamageTypes.IS_MAGIC)
                .remove(Tags.DamageTypes.IS_POISON)
                .remove(Tags.DamageTypes.IS_WITHER);
        this.tag(RERunes.MELEE_PROTECTION.get().getDamageTypeTag())
                .add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.STING);
    }

    public String getName() {
        return RuneEnchanting.MOD_ID + " Damage Type Tags";
    }
}
