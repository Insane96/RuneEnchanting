package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfBloodPact extends Rune {
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, RuneEnchanting.id("blood_pact"));

    @Override
    public String getName() {
        return "Curse of Blood Pact";
    }

    @Override
    public String getDescription() {
        return "Using the item can hurt the user";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.VANISHING_ENCHANTABLE);
    }

    @Override
    public void onItemHurt(HurtItemStackEvent event, ItemStack stack) {
        if (!(event.getLivingEntity() instanceof Player player))
            return;

        float damageAmount = 0f;
        for (int i = 0; i < event.getAmount(); i++) {
            if (event.getRandom().nextInt(10) == 0)
                damageAmount++;
        }
        if (damageAmount > 0)
            player.hurt(player.damageSources().source(DAMAGE_TYPE), damageAmount);
    }
}
