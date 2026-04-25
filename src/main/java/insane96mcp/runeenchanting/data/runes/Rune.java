package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.AttackContext;
import insane96mcp.runeenchanting.data.MiningContext;
import insane96mcp.runeenchanting.data.TickContext;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Rune {
    private final int priority;
    @Nullable
    private String descriptionId;
    private final Map<Field, ModConfigSpec.ConfigValue<?>> configValues = new LinkedHashMap<>();

    public Rune(int priority) {
        this.priority = priority;
    }

    public MutableComponent getName() {
        return Component.translatable(this.getNameLangId());
    }

    public MutableComponent getDescription() {
        return Component.translatable(this.getDescriptionLangId());
    }

    protected String getOrCreateNameLangId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("rune", RERunes.REGISTRY.getKey(this));
        }

        return this.descriptionId;
    }

    public String getNameLangId() {
        return this.getOrCreateNameLangId();
    }

    public String getDescriptionLangId() {
        return this.getOrCreateNameLangId() + ".description";
    }

    public ResourceLocation getApplicableToItemTag() {
        ResourceLocation id = RERunes.REGISTRY.getKey(this);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "rune_appliable_to/" + id.getPath());
    }

    public float onMiningSpeed(MiningContext context) {
        return context.originalSpeed;
    }

    public float onAttack(AttackContext context) {
        return context.damage;
    }

    public void onLivingTick(TickContext context) {

    }

    public void addAttributeModifiers(ItemAttributeModifierEvent event) {

    }

    public int getPriority() {
        return this.priority;
    }

    public void loadConfig(ModConfigSpec.Builder builder) {
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

    //TODO Add a method to override that fills the default applicable to item tag
}
