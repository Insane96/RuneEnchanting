package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import javax.annotation.Nullable;

public class HoppyRune extends Rune {
    @Config
    public static Double bonusJumpHeight = 0.4d;
    @Config
    public static Double fallDistanceReduction = 2d;

    @Override
    public String getName() {
        return "Hoppy";
    }

    @Override
    public String getDescription() {
        return "Increased jump height";
    }

    @Override
    public @Nullable String getInfo() {
        return "Fall distance reduction: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.JUMP_STRENGTH, new AttributeModifier(RuneEnchanting.id("hoppy"), bonusJumpHeight, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(RuneEnchanting.getEquipmentSlotForItem(event.getItemStack())));
    }

    @Override
    public void onLivingFall(LivingFallEvent event, ItemStack stack) {
        event.setDistance(event.getDistance() - fallDistanceReduction.floatValue());
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(fallDistanceReduction));
    }
}
