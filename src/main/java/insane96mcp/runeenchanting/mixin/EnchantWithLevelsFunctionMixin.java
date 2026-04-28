package insane96mcp.runeenchanting.mixin;

import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EnchantWithLevelsFunction.class)
public class EnchantWithLevelsFunctionMixin {
    @Shadow
    @Final
    private NumberProvider levels;

    @Shadow
    @Final
    private Optional<HolderSet<Enchantment>> options;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void runeenchanting$onRun(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        RandomSource randomSource = context.getRandom();
        int amount = MathHelper.getAmountWithDecimalChance(randomSource, this.levels.getFloat(context) / 10f);
        RuneHelper.addRandomRunes(stack, amount, randomSource, this.options);
        if (RuneFeature.disableExperience) {
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }
}
