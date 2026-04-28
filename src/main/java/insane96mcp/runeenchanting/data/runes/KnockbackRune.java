package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KnockbackRune extends Rune {
    @Config
    public static Double meleeKnockback = 0.8d;
    @Config
    public static Double arrowKnockback = 0.4d;

    @Override
    public String getName() {
        return "Knockback";
    }

    @Override
    public String getDescription() {
        return "Increases knockback of weapons and arrows";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.BOW);
    }

    @Override
    public float modifyKnockback(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original, float knockback) {
        if (damageSource.getDirectEntity() != damageSource.getEntity())
            return knockback + arrowKnockback.floatValue();
        return knockback + meleeKnockback.floatValue();
    }
}
