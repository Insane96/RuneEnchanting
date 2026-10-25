package insane96mcp.runeenchanting.runes;

import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class GravityDefyingRune extends Rune {
    public static final String NBT_TAG = "runeenchanting:gravity_defying";

    @Override
    public String getName() {
        return "Gravity Defying";
    }

    @Override
    public String getDescription() {
        return "Arrows fly in a flat trajectory, gradually decelerating";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(REItemTagProvider.RANGED_WEAPONS);
    }

    @Override
    public void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        arrow.setNoGravity(true);
        arrow.getPersistentData().putBoolean(NBT_TAG, true);
    }
}
