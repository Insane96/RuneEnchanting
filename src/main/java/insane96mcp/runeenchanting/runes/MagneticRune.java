package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.List;

public class MagneticRune extends Rune {
    @Config
    public static Double range = 4d;
    @Config
    public static Double strength = 0.05d;

    @Override
    public String getName() {
        return "Magnetic";
    }

    @Override
    public String getDescription() {
        return "Attracts nearby items";
    }

    @Override
    public @Nullable String getInfo() {
        return "Range: %s, Pull Strength: %s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        List<ItemEntity> itemsInRange = entity.level().getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(range));
        for (ItemEntity itemEntity : itemsInRange) {
            Vec3 vecToEntity = new Vec3(entity.getX() - itemEntity.getX(), (entity.getY() + (double)entity.getEyeHeight() / 2.0D) - itemEntity.getY(), entity.getZ() - itemEntity.getZ());
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(vecToEntity.normalize().scale(strength)));
            itemEntity.hurtMarked = true;
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(range), IAttributeExtension.FORMAT.format(strength * 100));
    }
}
