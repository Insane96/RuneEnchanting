package insane96mcp.runeenchanting.mixin;

import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @Shadow
    @Final
    private Optional<HolderSet<Enchantment>> options;

}
