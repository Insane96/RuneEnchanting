package insane96mcp.runeenchanting.runes;

import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public abstract class DamageReductionRune extends Rune {
    @Nullable
    private TagKey<DamageType> damageTypeTag;

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(REItemTagProvider.HORSE_ARMOR);
    }

    public abstract float damageReduction();

    @Override
    public @Nullable String getInfo() {
        return "Damage reduction: %s%% (max 80%% combined)";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damageReduction() * 100));
    }

    /**
     * If true, {@link #getDamageTypeTag()} is a blacklist (the rune protects from every damage type except the ones in the tag), otherwise it's a whitelist
     */
    public boolean isBlacklist() {
        return false;
    }

    /**
     * The damage type tag this rune protects from (or doesn't protect from, if {@link #isBlacklist()}).<br>
     * <code>runeenchanting:protected_by/&lt;rune&gt;</code> for whitelists, <code>runeenchanting:not_protected_by/&lt;rune&gt;</code> for blacklists
     */
    public TagKey<DamageType> getDamageTypeTag() {
        if (this.damageTypeTag == null) {
            ResourceLocation id = RERunes.REGISTRY.getKey(this);
            this.damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), (this.isBlacklist() ? "not_protected_by/" : "protected_by/") + id.getPath()));
        }
        return this.damageTypeTag;
    }

    public boolean shouldApply(DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        return damageSource.is(this.getDamageTypeTag()) != this.isBlacklist();
    }

    @Override
    public float modifyDamageProtection(ServerLevel level, ItemStack stack, LivingEntity entity, DamageSource damageSource, float original, float protection) {
        if (!shouldApply(damageSource))
            return protection;
        return protection + (this.damageReduction() * 25);
    }
}
