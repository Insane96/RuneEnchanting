package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class CurseOfEnderRune extends Rune {
    @Config(min = 0, max = 1)
    public static Double chanceToTeleport = 0.2d;

    @Override
    public String getName() {
        return "Curse of Ender";
    }

    @Override
    public String getDescription() {
        return "Randomly teleport when hurt";
    }

    @Override
    public String getInfo() {
        return "Chance to teleport: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM)
            return;
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide
                || entity.getRandom().nextFloat() >= chanceToTeleport)
            return;
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        Level level = event.getEntity().level();
        for (int i = 0; i < 16; ++i) {
            double tpX = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 12.0D;
            double tpY = Mth.clamp(entity.getY() + (double)(entity.getRandom().nextInt(12) - 6), level.getMinBuildHeight(), (level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
            double tpZ = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 12.0D;
            if (entity.isPassenger())
                entity.stopRiding();

            Vec3 vec3 = entity.position();
            level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
            if (entity.randomTeleport(tpX, tpY, tpZ, true)) {
                SoundEvent soundevent = entity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                level.playSound(null, x, y, z, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.playSound(soundevent, 1.0F, 1.0F);
                break;
            }
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(chanceToTeleport * 100f));
    }
}
