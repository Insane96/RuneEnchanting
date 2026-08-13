package insane96mcp.runeenchanting.runes;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Collection;

public class TelekinesisRune extends Rune {
    @Override
    public String getName() {
        return "Telekinesis";
    }

    @Override
    public String getDescription() {
        return "Sends drops from mined blocks and killed mobs directly to your inventory";
    }

    // Run after all the other runes so they can change drops first
    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.TRIDENT, Items.BOW, Items.CROSSBOW, Items.SHEARS);
    }

    @Override
    public void onBlockDrops(BlockDropsEvent event, ItemStack stack) {
        if (!(event.getBreaker() instanceof Player player)) return;
        collectIntoInventory(player, event.getDrops());
    }

    @Override
    public void onLivingDrops(LivingDropsEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        collectIntoInventory(player, event.getDrops());
    }

    private static void collectIntoInventory(Player player, Collection<ItemEntity> drops) {
        drops.removeIf(itemEntity -> {
            ItemStack itemStack = itemEntity.getItem();
            int originalCount = itemStack.getCount();
            player.getInventory().add(itemStack);
            if (itemStack.getCount() != originalCount) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                        ((player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            return itemStack.isEmpty();
        });
    }
}
