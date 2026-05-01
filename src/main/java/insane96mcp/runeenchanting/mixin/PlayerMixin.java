package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.runeenchanting.RuneHooks;
import insane96mcp.runeenchanting.setup.REAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEnchantedDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F"))
    public float runeenchanting$modifyDamage(Player player, Entity attacked, float damage, DamageSource damageSource, Operation<Float> original, @Local ItemStack stack) {
        float enchantmentDamage = original.call(player, attacked, damage, damageSource);
        return RuneHooks.onEnchantmentDamage(enchantmentDamage, player, attacked, damage, damageSource, stack);
    }

    @WrapOperation(method = "getDigSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    public float runeenchanting$modifyDigSpeed(Inventory inventory, BlockState state, Operation<Float> original, @Local(argsOnly = true) @Nullable BlockPos pos) {
        float f = original.call(inventory, state);
        Player player = (Player) (Object) this;
        return RuneHooks.onMiningSpeed(f, player, player.getMainHandItem(), state, pos);
    }

    @ModifyExpressionValue(method = "getDigSpeed", at = @At(value = "CONSTANT", args = "floatValue=5.0"))
    public float runeenchanting$onOffGroundMiningSpeedPenalty(float original) {
        Player player = (Player) (Object) this;
        AttributeInstance instance = player.getAttribute(REAttributes.OFF_GROUND_MINING_SPEED);
        if (instance == null) return original;
        double value = instance.getValue();
        if (value <= 0.0) return original;
        return (float) (1.0 / value);
    }
}