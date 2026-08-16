package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfSlipperinessRune extends Rune {
    @Config(min = 0, max = 1)
    public static Double chance = 0.025d;

    @Override
    public String getName() {
        return "Curse of Slipperiness";
    }

    @Override
    public String getDescription() {
        return "The item may slip off your hand and fall to the ground when used";
    }

    @Override
    public String getInfo() {
        return "Chance to slip off hand: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS);
        appender.addTag(ItemTags.MINING_ENCHANTABLE);
    }

    @Override
    public void onItemHurt(HurtItemStackEvent event, ItemStack stack) {
        if (!(event.getLivingEntity() instanceof Player player))
            return;

        boolean slips = false;
        for (int i = 0; i < event.getAmount(); i++) {
            if (event.getRandom().nextFloat() < chance.floatValue()) {
                slips = true;
                break;
            }
        }
        if (!slips)
            return;

        EquipmentSlot slot;
        if (player.getItemBySlot(EquipmentSlot.MAINHAND) == stack)
            slot = EquipmentSlot.MAINHAND;
        else if (player.getItemBySlot(EquipmentSlot.OFFHAND) == stack)
            slot = EquipmentSlot.OFFHAND;
        else
            return;

        player.setItemSlot(slot, ItemStack.EMPTY);
        player.drop(stack, true, false);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ALLAY_HURT, SoundSource.PLAYERS, 1f, 1.5f);
        event.setAmount(0);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.ONE_DECIMAL_FORMATTER.format(chance * 100f));
    }
}
