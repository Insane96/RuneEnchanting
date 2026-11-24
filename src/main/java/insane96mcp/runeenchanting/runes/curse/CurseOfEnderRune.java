package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Set;

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
        appender.addTag(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(REItemTagProvider.HORSE_ARMOR)
                .addTag(REItemTagProvider.WOLF_ARMOR);
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, EnchantmentTarget target) {
        if (target != EnchantmentTarget.VICTIM)
            return;
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide
                || event.getNewDamage() <= 0
                || entity.getRandom().nextFloat() >= chanceToTeleport)
            return;

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
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), RESoundEvents.CURSE_OF_ENDER.get(), entity.getSoundSource(), 1.0F, 1.0F);
                //Look in a random direction. Absolute rotation (no relative movements) so players get it sent to the client
                float yRot = entity.getRandom().nextFloat() * 360f - 180f;
                float xRot = entity.getRandom().nextFloat() * 180f - 90f;
                entity.teleportTo((ServerLevel) level, entity.getX(), entity.getY(), entity.getZ(), Set.of(), yRot, xRot);
                entity.setYBodyRot(yRot);
                break;
            }
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(chanceToTeleport * 100f));
    }
}
