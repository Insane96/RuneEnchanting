package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class HunterRune extends Rune {
    private static final String NBT_SPAWN_X = "runeenchanting:hunter_spawn_x";
    private static final String NBT_SPAWN_Y = "runeenchanting:hunter_spawn_y";
    private static final String NBT_SPAWN_Z = "runeenchanting:hunter_spawn_z";

    @Config(description = "Percentage damage penalty applied at point-blank range", min = 0, max = 1)
    public static Double startingPenalty = 0.3d;
    @Config(description = "Percentage bonus damage per block traveled", min = 0)
    public static Double damagePerBlock = 0.025d;

    @Override
    public String getName() {
        return "Hunter";
    }

    @Override
    public String getDescription() {
        return "Arrows deal more damage the farther they travel, but hit weaker at point-blank range";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.BOW, Items.CROSSBOW);
    }

    @Override
    public void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        CompoundTag data = arrow.getPersistentData();
        data.putDouble(NBT_SPAWN_X, arrow.getX());
        data.putDouble(NBT_SPAWN_Y, arrow.getY());
        data.putDouble(NBT_SPAWN_Z, arrow.getZ());
    }

    @Override
    public float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage, float damage) {
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow arrow))
            return damage;
        CompoundTag data = arrow.getPersistentData();
        if (!data.contains(NBT_SPAWN_X))
            return damage;
        Vec3 spawnPos = new Vec3(data.getDouble(NBT_SPAWN_X), data.getDouble(NBT_SPAWN_Y), data.getDouble(NBT_SPAWN_Z));
        double distanceTraveled = arrow.position().distanceTo(spawnPos);
        double bonusPercent = distanceTraveled * damagePerBlock - startingPenalty;
        return (float) Math.max(0d, damage + originalDamage * bonusPercent);
    }

    @Override
    public @Nullable String getInfo() {
        return "Bonus damage: %s%% per block, Starting penalty: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damagePerBlock * 100), IAttributeExtension.FORMAT.format(startingPenalty * 100));
    }
}
