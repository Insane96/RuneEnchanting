package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.AttackContext;
import insane96mcp.runeenchanting.data.MiningContext;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.Util;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Rune {
    private final int priority;
    @Nullable
    private String descriptionId;
    private final Map<Field, ModConfigSpec.ConfigValue<?>> configValues = new LinkedHashMap<>();

    private boolean enabled = true;
    private ModConfigSpec.BooleanValue enabledValue;

    public Rune() { this(0); }

    public Rune(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MutableComponent getNameComponent() {
        return Component.translatable(this.getNameTranslationKey());
    }

    public MutableComponent getDescriptionComponent() {
        return Component.translatable(this.getDescriptionTranslationKey());
    }

    ///Used for data gen
    public abstract String getName();
    ///Used for data gen
    public abstract String getDescription();

    protected String getOrCreateNameTanslationKey() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("rune", RERunes.REGISTRY.getKey(this));
        }

        return this.descriptionId;
    }

    public String getNameTranslationKey() {
        return this.getOrCreateNameTanslationKey();
    }

    public String getDescriptionTranslationKey() {
        return this.getOrCreateNameTanslationKey() + ".description";
    }

    public ResourceLocation getApplicableToItemTag() {
        ResourceLocation id = RERunes.REGISTRY.getKey(this);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "rune_applicable_to/" + id.getPath());
    }

    public abstract void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender);

    public float onMiningSpeed(MiningContext context) {
        return context.originalSpeed;
    }

    public float onAttack(AttackContext context) {
        return context.damage;
    }

    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {}

    public int modifyDurability(int original, ItemStack stack) {
        return original;
    }

    public void onEnchantmentLevel(GetEnchantmentLevelEvent event) {}

    public void addAttributeModifiers(ItemAttributeModifierEvent event) {}

    public float modifyEnchantmentDamage(Player player, Entity attacked, float damage, float originalDamage, DamageSource damageSource, ItemStack stack) {
        return damage;
    }

    public void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {}

    public void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {

    }

    public int modifyAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int originalCount, int count) {
        return count;
    }

    public int modifyProjectileCount(ServerLevel level, ItemStack tool, Entity entity, int originalCount, int count) {
        return count;
    }

    public float modifyProjectileSpread(ServerLevel level, ItemStack tool, Entity entity, float originalSpread, float spread) {
        return spread;
    }

    public int modifyDurabilityChange(ServerLevel level, ItemStack stack, int original, int damage) {
        return damage;
    }

    public int modifyBlockExperience(ServerLevel level, ItemStack stack, int original, int experience) {
        return experience;
    }

    public int modifyMobExperience(ServerLevel level, ItemStack stack, @Nullable Entity killer, Entity mob, int original, int experience) {
        return experience;
    }

    public boolean isImmuneToDamage(ServerLevel level, ItemStack stack, LivingEntity entity, DamageSource damageSource) {
        return false;
    }

    public float modifyDamageProtection(ServerLevel level, ItemStack stack, LivingEntity entity, DamageSource damageSource, float original, float protection) {
        return protection;
    }

    public float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original, float damage) {
        return damage;
    }

    public float modifyFallBasedDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original, float fallBasedDamage) {
        return fallBasedDamage;
    }

    public float modifyArmorEffectiveness(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original, float armorEffectiveness) {
        return armorEffectiveness;
    }

    public float modifyKnockback(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original, float knockback) {
        return knockback;
    }

    public int modifyPiercingCount(ServerLevel level, ItemStack firedFromWeapon, ItemStack pickupItemStack, int original, int count) {
        return count;
    }

    public void onHitBlock(ServerLevel level, ItemStack stack, @Nullable LivingEntity owner, Entity entity, @Nullable EquipmentSlot slot, Vec3 pos, BlockState state, Consumer<Item> onBreak) {}

    public float modifyFishingTimeReduction(ServerLevel level, ItemStack stack, Entity entity, float original, float reduction) {
        return reduction;
    }

    public int modifyTridentReturnToOwnerAcceleration(ServerLevel level, ItemStack stack, Entity entity, int original, int acceleration) {
        return acceleration;
    }

    public float modifyCrossbowChargingTime(ItemStack stack, LivingEntity entity, float original, float chargingTime) {
        return chargingTime;
    }

    public float modifyTridentSpinAttackStrength(ItemStack stack, LivingEntity entity, float original, float strength) {
        return strength;
    }

    public int getPriority() {
        return this.priority;
    }

    public void loadConfig(ModConfigSpec.Builder builder) {
        enabledValue = builder.define("Enabled", true);
        for (Field field : getClass().getDeclaredFields()) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation == null) continue;
            if (!Modifier.isStatic(field.getModifiers())) {
                RuneEnchanting.LOGGER.warn("@Config field {} in {} must be static, skipping", field.getName(), getClass().getName());
                continue;
            }
            field.setAccessible(true);
            String name = annotation.name().isEmpty() ? toConfigName(field.getName()) : annotation.name();
            if (!annotation.description().isEmpty())
                builder.comment(annotation.description());
            try {
                configValues.put(field, buildConfigValue(builder, name, annotation, field.get(null), field.getType()));
            } catch (IllegalAccessException e) {
                RuneEnchanting.LOGGER.error("Failed to read @Config field {} in {}", field.getName(), getClass().getName(), e);
            }
        }
    }

    public void readConfig() {
        enabled = enabledValue.get();
        configValues.forEach((field, value) -> {
            try {
                field.set(null, value.get());
            } catch (IllegalAccessException e) {
                RuneEnchanting.LOGGER.error("Failed to write @Config field {} in {}", field.getName(), getClass().getName(), e);
            }
        });
    }

    private ModConfigSpec.ConfigValue<?> buildConfigValue(ModConfigSpec.Builder builder, String name, Config annotation, Object def, Class<?> type) {
        if (type == double.class || type == Double.class)
            return builder.defineInRange(name, (double) def, annotation.min(), annotation.max());
        if (type == int.class || type == Integer.class)
            return builder.defineInRange(name, (int) def, (int) annotation.min(), (int) annotation.max());
        if (type == boolean.class || type == Boolean.class)
            return builder.define(name, (boolean) def);
        if (type == String.class)
            return builder.define(name, (String) def);
        throw new IllegalArgumentException("Unsupported @Config type: " + type.getName() + " in " + getClass().getName());
    }

    private static String toConfigName(String fieldName) {
        String spaced = fieldName.replaceAll("([A-Z])", " $1");
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
