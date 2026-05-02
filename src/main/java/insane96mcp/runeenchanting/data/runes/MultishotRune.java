package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import javax.annotation.Nullable;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

public class MultishotRune extends Rune {
    @Config
    public static Integer arrowsShot = 3;
    @Config
    public static Double arrowSpread = 10d;

    @Override
    public String getName() {
        return "Multishot";
    }

    @Override
    public String getDescription() {
        return "Shot 3 arrows at once";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.CROSSBOW);
    }

    @Override
    public int modifyAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int originalCount, int count) {
        return arrowsShot;
    }

    @Override
    public int modifyProjectileCount(ServerLevel level, ItemStack tool, Entity entity, int originalCount, int count) {
        return arrowsShot;
    }

    @Override
    public float modifyProjectileSpread(ServerLevel level, ItemStack tool, Entity entity, float originalSpread, float spread) {
        return arrowSpread.floatValue();
    }

    @Override
    public @Nullable String getInfo() {
        return "Arrows shot: %s. Spread: %s°";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), arrowsShot, IAttributeExtension.FORMAT.format(arrowSpread));
    }
}
