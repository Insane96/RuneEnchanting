package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LapisFueledRune extends Rune {
    private static final Predicate<ItemStack> IS_LAPIS = stack -> stack.is(Items.LAPIS_LAZULI);

    @Config(min = 0d, max = 1d, description = "Chance to not consume durability")
    public static Double saveChance = 0.667d;
    @Config(min = 0d, max = 1d, description = "Chance to consume a lapis lazuli when durability is saved")
    public static Double lapisConsumeChance = 0.1d;
    @Config(description = "If true, lapis lazuli inside shulker boxes and bundles in the inventory is used too. Lapis in the inventory is always consumed first")
    public static Boolean searchContainers = false;

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
            int lapisCount = countLapis(inventory);
            if (lapisCount <= 0)
                break;

            if (event.getRandom().nextFloat() >= saveChance.floatValue())
                continue;

            saved++;
            if (event.getRandom().nextFloat() < lapisConsumeChance.floatValue()) {
                consumeLapis(inventory);
                SoundEvent sound = lapisCount == 1 ? SoundEvents.BEACON_DEACTIVATE : SoundEvents.AMETHYST_BLOCK_CHIME;
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 0.4f, 1.7f);
            }
        }

        event.setAmount(amount - saved);
    }

    private static int countLapis(Inventory inventory) {
        int count = ContainerHelper.clearOrCountMatchingItems(inventory, IS_LAPIS, 0, true);
        if (!searchContainers)
            return count;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            if (container != null) {
                for (ItemStack item : container.nonEmptyItems()) {
                    if (IS_LAPIS.test(item))
                        count += item.getCount();
                }
            }
            BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle != null) {
                for (ItemStack item : bundle.items()) {
                    if (IS_LAPIS.test(item))
                        count += item.getCount();
                }
            }
        }
        return count;
    }

    private static void consumeLapis(Inventory inventory) {
        if (ContainerHelper.clearOrCountMatchingItems(inventory, IS_LAPIS, 1, false) > 0 || !searchContainers)
            return;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            if (container != null) {
                NonNullList<ItemStack> items = NonNullList.withSize(container.getSlots(), ItemStack.EMPTY);
                container.copyInto(items);
                for (ItemStack item : items) {
                    if (!IS_LAPIS.test(item))
                        continue;
                    item.shrink(1);
                    stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                    return;
                }
            }
            BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle != null) {
                List<ItemStack> items = new ArrayList<>(bundle.itemCopyStream().toList());
                for (int j = 0; j < items.size(); j++) {
                    ItemStack item = items.get(j);
                    if (!IS_LAPIS.test(item))
                        continue;
                    item.shrink(1);
                    if (item.isEmpty())
                        items.remove(j);
                    stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
                    return;
                }
            }
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(saveChance * 100), IAttributeExtension.FORMAT.format(lapisConsumeChance * 100));
    }
}
