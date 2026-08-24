package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BloodFueledRune extends Rune {
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, RuneEnchanting.id("blood_fueled"));

    @Config(min = 0d, max = 1d, description = "Chance to not consume durability")
    public static Double saveChance = 0.667d;
    @Config(min = 0d, max = 1d, description = "Chance to hurt the player when durability is saved, bypassing enchantments (e.g. protection)")
    public static Double selfDamageChance = 0.1d;

    @Override
    public String getName() {
        return "Blood-fueled";
    }

    @Override
    public String getDescription() {
        return "Chance to not consume durability, at the cost of a chance to hurt yourself with enchantment-bypassing damage";
    }

    @Override
    public String getInfo() {
        return "Chance to not consume durability: %s%%. Of those, chance to hurt yourself for 1 (bypasses enchantments): %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    @Override
    public void onItemHurt(HurtItemStackEvent event, ItemStack stack) {
        if (!(event.getLivingEntity() instanceof Player player))
            return;

        int amount = event.getAmount();
        int saved = 0;
        float damageAmount = 0f;
        for (int i = 0; i < amount; i++) {
            if (event.getRandom().nextFloat() >= saveChance.floatValue())
                continue;

            saved++;
            if (event.getRandom().nextFloat() < selfDamageChance.floatValue())
                damageAmount++;
        }

        event.setAmount(amount - saved);
        if (damageAmount > 0)
            player.hurt(player.damageSources().source(DAMAGE_TYPE), damageAmount);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(saveChance * 100), RuneEnchanting.NO_DECIMAL_FORMATTER.format(selfDamageChance * 100));
    }
}
