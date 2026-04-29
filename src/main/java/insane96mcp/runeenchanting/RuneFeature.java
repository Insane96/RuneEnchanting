package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.Module;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.IntegratedPack;
import insane96mcp.runeenchanting.data.runes.Rune;
import insane96mcp.runeenchanting.network.message.ClientboundDisableExperienceMessage;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@LoadFeature(canBeDisabled = false)
public class RuneFeature extends Feature {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLEEXPERIENCE = GameRules.register("runeenchanting:disable_experience", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, booleanValue) -> {
        RuneFeature.disableExperience = booleanValue.get();
        ClientboundDisableExperienceMessage.sync(booleanValue.get());
        server.getPlayerList().getPlayers().forEach(player -> {
            if (booleanValue.get())
                player.setExperienceLevels(9999);
            else
                player.setExperienceLevels(0);
        });
    }));

    @Config
    public static Boolean hideCurses = true;
    @Config
    public static Boolean extractCurses = false;
    @Config(description = "Vanilla uses enchant_with_levels loot function for loot tables and mobs equipment. The mod takes the levels specified by the loot function and will divide them by this value, resulting in how many runes will be applied to the item (limited by the sockets). Decimal portions are treated as a chance for a +1 rune (so if the result of the division is 1.8, there's 20% chance to have 1 rune and 80% for 2)")
    public static Double enchantWithLevelsFunctionRatio = 15d;
    @Config
    public static Boolean integratedDataPack = true;

    public static Boolean disableExperience = false;

    @Override
    public void init(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super.init(module, enabledByDefault, canBeDisabled);
        IntegratedPack.addServerPack(RuneEnchanting.MOD_ID, "enchantment_changes", "Rune Enchanting vanilla changes", () -> integratedDataPack);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb)
                || !disableExperience)
            return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!disableExperience
                || !event.getItemStack().is(Items.EXPERIENCE_BOTTLE))
            return;

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ClientboundDisableExperienceMessage.sync((ServerPlayer) event.getEntity(), disableExperience);
        if (disableExperience)
            ((ServerPlayer) event.getEntity()).setExperienceLevels(9999);
        else
            ((ServerPlayer) event.getEntity()).setExperienceLevels(0);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void removeExperienceBar(final RenderGuiLayerEvent.Pre event) {
        if (!disableExperience)
            return;

        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) || event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            event.setCanceled(true);
            if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
                Minecraft.getInstance().gui.leftHeight -= 6;
                Minecraft.getInstance().gui.rightHeight -= 6;
            }
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().isEmpty() || !event.getRight().is(REItems.RUNE))
            return;
        ItemStack output = event.getLeft().copy();
        Holder<Rune> toApply = event.getRight().get(REDataComponents.STORED_RUNE.value());
        if (toApply == null
                || !toApply.value().canBeAppliedTo(output))
            return;
        if (!RuneHelper.addRune(output, toApply))
            return;
        event.setOutput(output);
        event.setCost(1);
    }

    @SubscribeEvent
    public void onGrindstonePlaceItem(GrindstoneEvent.OnPlaceItem event) {
        if (event.getTopItem().isEmpty()
                || !event.getBottomItem().isEmpty())
            return;
        ItemStack output = event.getTopItem().copy();
        List<Holder<Rune>> removedRunes = RuneHelper.clearRunes(output, extractCurses);
        if (removedRunes.isEmpty())
            return;
        event.setOutput(output);
        event.setXp(0);
    }

    @SubscribeEvent
    public void onGrindstoneTakeItem(GrindstoneEvent.OnTakeItem event) {
        if (event.getTopItem().isEmpty()
                || !event.getBottomItem().isEmpty())
            return;
        ItemStack output = event.getTopItem().copy();
        List<Holder<Rune>> removedRunes = RuneHelper.clearRunes(output, extractCurses);
        if (removedRunes.isEmpty())
            return;
        event.getContainerAccess().execute((world, pos) -> {
            for (Holder<Rune> holder : removedRunes) {
                ItemStack runeStack = new ItemStack(REItems.RUNE, 1, DataComponentPatch.builder().set(REDataComponents.STORED_RUNE.value(), holder).build());
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, runeStack);
                itemEntity.setDefaultPickUpDelay();
                world.addFreshEntity(itemEntity);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RECommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onStackAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().addAttributeModifiers(event);
        }
    }

    @SubscribeEvent
    public void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
        ItemStack stack = event.getStack();
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onEnchantmentLevel(event);
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int sockets = stack.getOrDefault(REDataComponents.SOCKETS, 0);
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack, false);
        int runesCount = RuneHelper.countRunes(stack);
        if (sockets > 0 && !stack.is(REItems.RUNE)) {
            if (runesCount > 0 || event.getFlags().hasShiftDown()) {
                event.getToolTip().add(CommonComponents.space());
                event.getToolTip().add(Component.translatable("sockets", runesCount, sockets).withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
        boolean isCursed = false;
        if (runes != null) {
            for (Holder<Rune> holder : runes) {
                if (holder.value().isCurse() && hideCurses)
                    isCursed = true;
                else {
                    event.getToolTip().add(CommonComponents.space().append(holder.value().getNameComponent().withStyle(ChatFormatting.LIGHT_PURPLE)));
                    if (event.getFlags().hasShiftDown())
                        event.getToolTip().add(CommonComponents.space().append(holder.value().getDescriptionComponent()).withStyle(ChatFormatting.GRAY));
                }
            }
            if (isCursed) {
                if (stack.is(REItems.RUNE))
                    event.getToolTip().add(CommonComponents.space().append(Component.translatable("curse").withStyle(ChatFormatting.RED)));
                else
                    event.getToolTip().add(CommonComponents.space().append(Component.translatable("cursed").withStyle(ChatFormatting.RED)));
                if (event.getFlags().hasShiftDown())
                    event.getToolTip().add(CommonComponents.space().append(Component.translatable("cursed_info")).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int onGetMaxDamage(int original, ItemStack stack) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int result = original;
        for (Holder<Rune> holder : runes) {
            result = holder.value().modifyDurability(result, stack);
        }
        return result;
    }

    public static float onEnchantmentDamage(float enchantmentDamage, Player player, Entity attacked, float originalDamage, DamageSource damageSource, ItemStack stack) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return originalDamage;
        float damage = originalDamage;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyEnchantmentDamage(player, attacked, damage, originalDamage, damageSource, stack);
        }
        return damage;
    }
    public static void onProjectileSpawned(ServerLevel level, ItemStack stack, AbstractArrow arrow, Consumer<Item> onBreak) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onProjectileSpawned(level, stack, arrow, onBreak);
        }
    }

    public static void onPostAttack(ServerLevel level, @Nullable ItemStack stack, EnchantmentTarget target, Entity attacked, DamageSource damageSource) {
        if (stack == null)
            return;
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onPostAttack(level, stack, target, attacked, damageSource);
        }
    }

    public static int modifyAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int originalCount) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(weapon);
        if (runes == null)
            return originalCount;
        int count = originalCount;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyAmmoUse(level, weapon, ammo, originalCount, count);
        }
        return count;
    }

    public static int modifyProjectileCount(ServerLevel level, ItemStack tool, Entity entity, int originalCount) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalCount;
        int count = originalCount;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyProjectileCount(level, tool, entity, originalCount, count);
        }
        return count;
    }
    public static float modifyProjectileSpread(ServerLevel level, ItemStack tool, Entity entity, float originalSpread) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalSpread;
        float spread = originalSpread;
        for (Holder<Rune> holder : runes) {
            spread = holder.value().modifyProjectileSpread(level, tool, entity, originalSpread, spread);
        }
        return spread;
    }

    public static int modifyDurabilityChange(ServerLevel level, ItemStack stack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int damage = original;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyDurabilityChange(level, stack, original, damage);
        }
        return damage;
    }

    public static int modifyBlockExperience(ServerLevel level, ItemStack stack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int experience = original;
        for (Holder<Rune> holder : runes) {
            experience = holder.value().modifyBlockExperience(level, stack, original, experience);
        }
        return experience;
    }

    public static int modifyMobExperience(int original, ServerLevel level, @Nullable Entity killer, Entity mob) {
        if (!(killer instanceof LivingEntity livingKiller))
            return original;
        int experience = original;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = livingKiller.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                experience = holder.value().modifyMobExperience(level, stack, killer, mob, original, experience);
            }
        }
        return experience;
    }

    public static boolean isImmuneToDamage(ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                if (holder.value().isImmuneToDamage(level, stack, entity, damageSource))
                    return true;
            }
        }
        return false;
    }

    public static float modifyDamageProtection(float original, ServerLevel level, LivingEntity entity, DamageSource damageSource) {
        float protection = original;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                protection = holder.value().modifyDamageProtection(level, stack, entity, damageSource, original, protection);
            }
        }
        return protection;
    }

    public static float modifyDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalDamage;
        float damage = originalDamage;
        for (Holder<Rune> holder : runes) {
            damage = holder.value().modifyDamage(level, tool, entity, damageSource, originalDamage, damage);
        }
        return damage;
    }

    public static float modifyFallBasedDamage(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalDamage) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalDamage;
        float fallBasedDamage = originalDamage;
        for (Holder<Rune> holder : runes) {
            fallBasedDamage = holder.value().modifyFallBasedDamage(level, tool, entity, damageSource, originalDamage, fallBasedDamage);
        }
        return fallBasedDamage;
    }

    public static float modifyArmorEffectiveness(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float originalArmorEffectiveness) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return originalArmorEffectiveness;
        float armorEffectiveness = originalArmorEffectiveness;
        for (Holder<Rune> holder : runes) {
            armorEffectiveness = holder.value().modifyArmorEffectiveness(level, tool, entity, damageSource, originalArmorEffectiveness, armorEffectiveness);
        }
        return armorEffectiveness;
    }

    public static float modifyKnockback(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(tool);
        if (runes == null)
            return original;
        float knockback = original;
        for (Holder<Rune> holder : runes) {
            knockback = holder.value().modifyKnockback(level, tool, entity, damageSource, original, knockback);
        }
        return knockback;
    }

    public static void tickEffects(ServerLevel level, LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
            if (runes == null) continue;
            for (Holder<Rune> holder : runes) {
                holder.value().tickEffects(level, stack, entity);
            }
        }
    }

    public static int modifyPiercingCount(ServerLevel level, ItemStack firedFromWeapon, ItemStack pickupItemStack, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(firedFromWeapon);
        if (runes == null)
            return original;
        int count = original;
        for (Holder<Rune> holder : runes) {
            count = holder.value().modifyPiercingCount(level, firedFromWeapon, pickupItemStack, original, count);
        }
        return count;
    }

    public static void onHitBlock(ServerLevel level, ItemStack stack, @Nullable LivingEntity owner, Entity entity, @Nullable EquipmentSlot slot, Vec3 pos, BlockState state, Consumer<Item> onBreak) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            holder.value().onHitBlock(level, stack, owner, entity, slot, pos, state, onBreak);
        }
    }

    public static float modifyFishingTimeReduction(ServerLevel level, ItemStack stack, Entity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float reduction = original;
        for (Holder<Rune> holder : runes) {
            reduction = holder.value().modifyFishingTimeReduction(level, stack, entity, original, reduction);
        }
        return reduction;
    }

    public static int modifyTridentReturnToOwnerAcceleration(ServerLevel level, ItemStack stack, Entity entity, int original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        int acceleration = original;
        for (Holder<Rune> holder : runes) {
            acceleration = holder.value().modifyTridentReturnToOwnerAcceleration(level, stack, entity, original, acceleration);
        }
        return acceleration;
    }

    public static float modifyCrossbowChargingTime(ItemStack stack, LivingEntity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float chargingTime = original;
        for (Holder<Rune> holder : runes) {
            chargingTime = holder.value().modifyCrossbowChargingTime(stack, entity, original, chargingTime);
        }
        return chargingTime;
    }

    public static float modifyTridentSpinAttackStrength(ItemStack stack, LivingEntity entity, float original) {
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack);
        if (runes == null)
            return original;
        float strength = original;
        for (Holder<Rune> holder : runes) {
            strength = holder.value().modifyTridentSpinAttackStrength(stack, entity, original, strength);
        }
        return strength;
    }

}
