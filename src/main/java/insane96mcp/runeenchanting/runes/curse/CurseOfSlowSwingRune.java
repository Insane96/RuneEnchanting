package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

/// Merge of Curse of Inefficiency and Curse of Slow Strike: penalizes both roles of the item, so it can't be put for free on a tool used only as a weapon (or vice versa)
public class CurseOfSlowSwingRune extends Rune {

    @Config(min = 0, max = 1)
    public static Double miningSpeedReduction = 0.5d;
    @Config
    public static Double attackSpeedReduction = 0.2d;

    public CurseOfSlowSwingRune() {
        //Run after all the other efficiency runes
        super(1);
    }

    @Override
    public int getCurseLearnMultiplier() {
        return 2;
    }

    @Override
    public String getName() {
        return "Curse of Slow Swing";
    }

    @Override
    public String getDescription() {
        return "Decreases mining, brushing and attack speed";
    }

    @Override
    public @Nullable String getInfo() {
        return "Mining and brushing speed: -%s%%, Attack speed: -%s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(miningSpeedReduction * 100f), IAttributeExtension.FORMAT.format(attackSpeedReduction * 100));
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.WEAPONS)
                .addTag(ItemTags.MINING_ENCHANTABLE)
                .add(Items.BRUSH);
    }

    @Override
    public float onBrushSpeed(Player player, ItemStack stack, float speed) {
        return speed * (1f - miningSpeedReduction.floatValue());
    }

    @Override
    public float onMiningSpeed(Player player, ItemStack stack, BlockState state, @Nullable BlockPos pos, float original, float speed) {
        if (!stack.isCorrectToolForDrops(state))
            return speed;
        return speed * (1f - miningSpeedReduction.floatValue());
    }

    @Override
    public void addAttributeModifiers(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(RuneEnchanting.id("curse_of_slow_swing"), -attackSpeedReduction, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }
}
