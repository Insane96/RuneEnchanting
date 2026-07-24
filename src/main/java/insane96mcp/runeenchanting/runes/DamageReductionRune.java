package insane96mcp.runeenchanting.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public abstract class DamageReductionRune extends Rune {
    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    public abstract float damageReduction();

    @Override
    public @Nullable String getInfo() {
        return "Damage reduction: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damageReduction() * 100));
    }

    public boolean shouldApply(DamageSource damageSource) {
        return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public float modifyDamageProtection(ServerLevel level, ItemStack stack, LivingEntity entity, DamageSource damageSource, float original, float protection) {
        if (!shouldApply(damageSource))
            return protection;
        return protection + (this.damageReduction() * 25);
    }
}
