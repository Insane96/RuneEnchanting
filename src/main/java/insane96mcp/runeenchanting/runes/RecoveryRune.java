package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

public class RecoveryRune extends Rune {
    ///Stored on the entity rather than on the item: changing the item every second would count as an equipment change
    private static final ResourceLocation NBT_STORED_DAMAGE = RuneEnchanting.id("recovery/stored_damage");

    @Config
    public static Double damageToRegenRatio = 0.4d;
    @Config(description = "Per second")
    public static Double regenSpeed = 0.35d;

    @Override
    public String getName() {
        return "Recovery";
    }

    @Override
    public String getDescription() {
        return "Recover some health back overtime when attacked";
    }

    @Override
    public @Nullable String getInfo() {
        return "Damage to regen: %s%%. Regen speed: %s/s";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (!(event.getSource().getEntity() instanceof LivingEntity)
                || target != EnchantmentTarget.VICTIM)
            return;

        float damageToRegenStored = ModNBTData.get(event.getEntity(), NBT_STORED_DAMAGE, Float.class);
        float damageAmount = event.getNewDamage();
        if (damageAmount > event.getEntity().getHealth())
            damageAmount = event.getEntity().getHealth();
        float damageToRestore = damageAmount * damageToRegenRatio.floatValue();
        if (damageToRestore <= damageToRegenStored)
            return;
        ModNBTData.put(event.getEntity(), NBT_STORED_DAMAGE, damageToRestore);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 20 != 0)
            return;
        // Items saved before the damage was moved to the entity
        if (stack.has(REDataComponents.STORED_DAMAGE))
            stack.remove(REDataComponents.STORED_DAMAGE);
        float damageToRegenStored = ModNBTData.get(entity, NBT_STORED_DAMAGE, Float.class);
        if (damageToRegenStored <= 0)
            return;

        float toRegen = regenSpeed.floatValue();
        if (toRegen > damageToRegenStored)
            toRegen = damageToRegenStored;
        entity.heal(toRegen);
        damageToRegenStored -= toRegen;
        if (damageToRegenStored <= 0)
            ModNBTData.remove(entity, NBT_STORED_DAMAGE);
        else
            ModNBTData.put(entity, NBT_STORED_DAMAGE, damageToRegenStored);
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack) {
        return Component.translatable(getInfoTranslationKey(), damageToRegenRatio * 100f, IAttributeExtension.FORMAT.format(regenSpeed));
    }
}
