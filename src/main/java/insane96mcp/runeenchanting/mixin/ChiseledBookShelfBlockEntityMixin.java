package insane96mcp.runeenchanting.mixin;

import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Chiseled bookshelves have no loot table, so enchanted books stored in structures' NBT never go through the rune loot modifiers.
 */
@Mixin(ChiseledBookShelfBlockEntity.class)
public class ChiseledBookShelfBlockEntityMixin {
    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void runeenchanting$replaceEnchantedBooks(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (RuneFeature.disableExperience)
            RuneHelper.replaceEnchantedBooksWithRunes(this.items);
    }
}
