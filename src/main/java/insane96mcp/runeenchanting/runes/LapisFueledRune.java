package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class LapisFueledRune extends Rune {
    private static final Predicate<ItemStack> IS_LAPIS = stack -> stack.is(Items.LAPIS_LAZULI);

    @Config(min = 0d, max = 1d, description = "Chance to not consume durability")
    public static Double saveChance = 0.667d;
    @Config(min = 0d, max = 1d, description = "Chance to consume a lapis lazuli when durability is saved")
    public static Double lapisConsumeChance = 0.1d;

    @Override
    public String getName() {
        return "Lapis-fueled";
    }

    @Override
    public String getDescription() {
        return "Chance to not consume durability, at the cost of consuming lapis lazuli from the inventory";
    }

    @Override
    public @Nullable String getInfo() {
        return "Chance to not consume durability: %s%%. Of those, chance to consume a lapis lazuli: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.DURABILITY_ENCHANTABLE);
    }

    @Override
    public void onItemHurt(HurtItemStackEvent event, ItemStack stack) {
        if (!(event.getLivingEntity() instanceof Player player))
            return;

        Inventory inventory = player.getInventory();
        int amount = event.getAmount();
        int saved = 0;
        for (int i = 0; i < amount; i++) {
            if (ContainerHelper.clearOrCountMatchingItems(inventory, IS_LAPIS, 0, true) <= 0)
                break;

            if (event.getRandom().nextFloat() >= saveChance.floatValue())
                continue;

            saved++;
            if (event.getRandom().nextFloat() < lapisConsumeChance.floatValue())
                ContainerHelper.clearOrCountMatchingItems(inventory, IS_LAPIS, 1, false);
        }

        event.setAmount(amount - saved);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(saveChance * 100), IAttributeExtension.FORMAT.format(lapisConsumeChance * 100));
    }
}
