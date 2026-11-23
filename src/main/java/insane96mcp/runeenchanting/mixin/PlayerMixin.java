package insane96mcp.runeenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import insane96mcp.runeenchanting.RuneHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEnchantedDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F"))
    public float runeenchanting$modifyDamage(Player player, Entity attacked, float damage, DamageSource damageSource, Operation<Float> original, @Local ItemStack stack) {
        float enchantmentDamage = original.call(player, attacked, damage, damageSource);
        return RuneHooks.onEnchantmentDamage(enchantmentDamage, player, attacked, damage, damageSource, stack);
    }

    @WrapOperation(method = "getDigSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    public float runeenchanting$modifyDigSpeed(Inventory inventory, BlockState state, Operation<Float> original, @Local(argsOnly = true) @Nullable BlockPos pos, @Share("vanillaDestroySpeed") LocalFloatRef vanillaDestroySpeed) {
        float f = original.call(inventory, state);
        vanillaDestroySpeed.set(f);
        Player player = (Player) (Object) this;
        return RuneHooks.onMiningSpeed(f, player, player.getMainHandItem(), state, pos);
    }

    // Vanilla adds the Mining Efficiency attribute only if the destroy speed is > 1 (the tool is effective on the block). Runes can raise the speed above 1 on blocks the tool isn't effective on, so check the speed before runes
    @WrapOperation(method = "getDigSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    public double runeenchanting$noMiningEfficiencyOnIneffectiveTool(Player player, Holder<Attribute> attribute, Operation<Double> original, @Share("vanillaDestroySpeed") LocalFloatRef vanillaDestroySpeed) {
        if (attribute.is(Attributes.MINING_EFFICIENCY) && vanillaDestroySpeed.get() <= 1f)
            return 0d;
        return original.call(player, attribute);
    }

    // Player overrides Entity#playSound without calling super, so EntityMixin doesn't catch player sounds
    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"))
    private void runeenchanting$onPlaySound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!player.isSilent())
            RuneHooks.onEntitySound(player);
    }
}