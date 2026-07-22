package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

        float damageToRegenStored = stack.getOrDefault(REDataComponents.STORED_DAMAGE, 0f);
        float damageAmount = event.getNewDamage();
        if (damageAmount > event.getEntity().getHealth())
            damageAmount = event.getEntity().getHealth();
        float damageToRestore = damageAmount * damageToRegenRatio.floatValue();
        if (damageToRestore <= damageToRegenStored)
            return;
        stack.set(REDataComponents.STORED_DAMAGE, damageToRestore);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 20 != 0)
            return;
        float damageToRegenStored = stack.getOrDefault(REDataComponents.STORED_DAMAGE, 0f);
        if (damageToRegenStored <= 0)
            return;

        float toRegen = regenSpeed.floatValue();
        if (toRegen > damageToRegenStored)
            toRegen = damageToRegenStored;
        entity.heal(toRegen);
        damageToRegenStored -= toRegen;
        stack.set(REDataComponents.STORED_DAMAGE, damageToRegenStored);
    }

    @Override
    public MutableComponent getInfoComponent(ItemStack stack) {
        return Component.translatable(getInfoTranslationKey(), damageToRegenRatio * 100f, IAttributeExtension.FORMAT.format(regenSpeed));
    }
}
