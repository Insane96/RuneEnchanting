package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class PullerRune extends Rune {
    @Config(min = 1, description = "Multiplier to the strength hooked entities are pulled with")
    public static Double pullStrengthMultiplier = 1.5d;
    @Config(min = 0, description = "Durability consumed when pulling hooked entities and items (vanilla: 5 for entities, 3 for items)")
    public static Integer durabilityCost = 1;

    @Override
    public String getName() {
        return "Puller";
    }

    @Override
    public String getDescription() {
        return "Hooked mobs and items are pulled harder and consume less durability";
    }

    @Override
    public @Nullable String getInfo() {
        return "Pull strength: %sx, Durability cost: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.FISHING_ROD);
    }

    @Override
    public float modifyFishingPullStrength(ItemStack stack, Entity owner, Entity pulled, float original, float strength) {
        return strength + original * (pullStrengthMultiplier.floatValue() - 1f);
    }

    @Override
    public int modifyFishingPullDurabilityCost(ItemStack stack, Entity owner, Entity pulled, int original, int cost) {
        return Math.min(cost, durabilityCost);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(pullStrengthMultiplier), durabilityCost);
    }
}
