package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagneticRune extends Rune {
    @Config
    public static Double range = 4d;
    @Config
    public static Double strength = 0.05d;

    @Override
    public String getName() {
        return "Charged Jump";
    }

    @Override
    public String getDescription() {
        return "Crouch for a few seconds to charge up a strong jump";
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
}
