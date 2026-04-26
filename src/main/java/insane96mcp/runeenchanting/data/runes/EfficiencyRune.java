package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class EfficiencyRune extends Rune {
    @Config(description = "Percentage bonus mining speed")
    public static Double bonusMiningSpeed = 1d;
    @Config
    public static Double bonusFlatMiningSpeed = 2.5d;

    public EfficiencyRune(int priority) {
        super(priority);
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES)
                .add(Items.SHEARS);
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        float miningSpeed = 0f;
        if (event.getItemStack().getItem() instanceof TieredItem tieredItem)
            miningSpeed = tieredItem.getTier().getSpeed() * bonusMiningSpeed.floatValue();
        event.addModifier(Attributes.MINING_EFFICIENCY, new AttributeModifier(RuneEnchanting.location("efficiency"), miningSpeed + bonusFlatMiningSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
