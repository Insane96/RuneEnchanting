package insane96mcp.runeenchanting.data.runes;

import com.mojang.blaze3d.platform.InputConstants;
import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public class DoubleJumpRune extends Rune {
    @Config
    public static Integer extraJumps = 1;

    private static final String DOUBLE_JUMPS_KEY = "runeenchanting:double_jumps";

    @Override
    public String getName() {
        return "Double Jump";
    }

    @Override
    public String getDescription() {
        return "Press jump while in the air to jump again";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.Key event, ItemStack stack) {
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.keyJump.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode())))
            return;
        Player player = mc.player;
        if (player == null)
            return;
        if (player.onGround() || player.onClimbable() || player.isInWaterOrBubble() || player.getVehicle() != null) {
            player.getPersistentData().remove(DOUBLE_JUMPS_KEY);
            return;
        }
        int jumpsUsed = player.getPersistentData().getInt(DOUBLE_JUMPS_KEY);
        if (jumpsUsed >= extraJumps)
            return;

        player.jumpFromGround();
        player.getPersistentData().putInt(DOUBLE_JUMPS_KEY, jumpsUsed + 1);
        player.fallDistance = 0f;

        RandomSource random = player.level().getRandom();
        for (int i = 0; i < 5; i++) {
            player.level().addParticle(ParticleTypes.CLOUD,
                    player.getX() - 0.25f + random.nextFloat() * 0.5f, player.getY(),
                    player.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
        }
        player.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.5f, 2f);
    }

    @Override
    public void tickEffects(ServerLevel level, ItemStack stack, LivingEntity entity) {
        if (entity.onGround())
            entity.getPersistentData().remove(DOUBLE_JUMPS_KEY);
    }
}
