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
    public static final TagKey<Rune> ARMOR_HEAD_ONLY = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("armor_head_only"));
    public static final TagKey<Rune> ARMOR_CHEST_ONLY = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("armor_chest_only"));
    public static final TagKey<Rune> ARMOR_LEGS_ONLY = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("armor_legs_only"));
    public static final TagKey<Rune> ARMOR_FEET_ONLY = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("armor_feet_only"));
    public static final TagKey<Rune> ARMOR_ONLY = TagKey.create(RERunes.REGISTRY_KEY, RuneEnchanting.id("armor_only"));
    public static final TagKey<Rune> LOOT_ABANDONED_MINESHAFT = TagKey.create(RERunes.REGISTRY_KEY, loot("abandoned_mineshaft"));
    public static final TagKey<Rune> LOOT_IGLOO_CHEST = TagKey.create(RERunes.REGISTRY_KEY, loot("igloo_chest"));
    public static final TagKey<Rune> LOOT_ANCIENT_CITY_SPECIFIC = TagKey.create(RERunes.REGISTRY_KEY, loot("ancient_city_specific"));
    public static final TagKey<Rune> LOOT_ANCIENT_CITY = TagKey.create(RERunes.REGISTRY_KEY, loot("ancient_city"));
    public static final TagKey<Rune> BASTION_OTHER_SPECIFIC = TagKey.create(RERunes.REGISTRY_KEY, loot("ancient_city_specific"));

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

        add(ARMOR_HEAD_ONLY,
                RERunes.RESPIRATION,
                RERunes.AQUA_AFFINITY);

        add(ARMOR_CHEST_ONLY,
                RERunes.AIR_AFFINITY,
                RERunes.VINDICATION,
                RERunes.RECOVERY);

        add(ARMOR_LEGS_ONLY,
                RERunes.SWIFT_SNEAK,
                RERunes.STEP_UP,
                RERunes.ZIPPY,
                RERunes.SPRINT_PACT,
                RERunes.CHARGED_JUMP,
                RERunes.RETREAT);

        add(ARMOR_FEET_ONLY,
                RERunes.FEATHER_FALLING,
                RERunes.DEPTH_STRIDER,
                RERunes.FROST_WALKER,
                RERunes.SOUL_SPEED,
                RERunes.HOPPY,
                RERunes.DOUBLE_JUMP,
                RERunes.STEADY_FALL);

        add(ARMOR_ONLY,
                RERunes.PROTECTION,
                RERunes.BLAST_PROTECTION,
                RERunes.FIRE_PROTECTION,
                RERunes.PROJECTILE_PROTECTION,
                RERunes.MELEE_PROTECTION,
                RERunes.MAGIC_PROTECTION,
                RERunes.HEALTHY,
                RERunes.THORNS,
                RERunes.MAGNETIC)
                .addTag(ARMOR_HEAD_ONLY)
                .addTag(ARMOR_CHEST_ONLY)
                .addTag(ARMOR_LEGS_ONLY)
                .addTag(ARMOR_FEET_ONLY);

        add(LOOT_ABANDONED_MINESHAFT,
                RERunes.EFFICIENCY,
                RERunes.BLASTING,
                RERunes.SILK_TOUCH,
                RERunes.EARTHBEND,
                RERunes.DWARFING,
                RERunes.LUCK);

        add(LOOT_IGLOO_CHEST,
                RERunes.FROST_WALKER);

        add(LOOT_ANCIENT_CITY_SPECIFIC,
                RERunes.SWIFT_SNEAK);
        add(LOOT_ANCIENT_CITY)
                .addTag(ARMOR_LEGS_ONLY);

        add(BASTION_OTHER_SPECIFIC,
                RERunes.SOUL_SPEED);
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
