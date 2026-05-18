package insane96mcp.runeenchanting.runes.curse;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.Rune;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurseOfHopRune extends Rune {
    @Config(min = 0, max = 1)
    public static Double jumpChance = 0.05d;
    @Config(description = "Side strength of the jump")
    public static Double strength = 0.5d;

    @Override
    public String getName() {
        return "Curse of Hop";
    }

    @Override
    public String getDescription() {
        return "Randomly jump";
    }

    @Override
    public String getInfo() {
        return "Jump chance each second: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if ((entity.tickCount + entity.getId()) % 20 != 0
                || !entity.onGround())
            return;

        if (entity.getRandom().nextFloat() <= jumpChance) {
            if (entity instanceof Player player)
                player.jumpFromGround();
            else if (entity instanceof Mob mob)
                mob.getJumpControl().jump();
            float rot = entity.getYRot();
            if (entity.getRandom().nextBoolean())
                rot = (rot + 180) % 360;
            float radian = rot * ((float)Math.PI / 180F);
            entity.setDeltaMovement(entity.getDeltaMovement().add(-Mth.sin(radian) * strength, 0.0D, Mth.cos(radian) * strength));
            entity.setJumping(true);
            entity.hurtMarked = true;
        }
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.ONE_DECIMAL_FORMATTER.format(jumpChance * 100f));
    }
}
