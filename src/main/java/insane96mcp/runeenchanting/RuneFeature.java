package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.insanelib.module.base.items.ItemComponentsReloadListener;
import insane96mcp.insanelib.setup.ILAttributes;
import insane96mcp.insanelib.util.CurrentAttacker;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.runeenchanting.data.provider.REItemTagProvider;
import insane96mcp.runeenchanting.network.message.ClientboundDisableExperienceMessage;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.runes.curse.CurseOfExposureRune;
import insane96mcp.runeenchanting.setup.RECriteriaTriggers;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import insane96mcp.runeenchanting.util.CurseGlitch;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@LoadFeature(module = RuneEnchanting.MAIN_MODULE, canBeDisabled = false)
public class RuneFeature extends Feature {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLEEXPERIENCE = GameRules.register(RuneEnchanting.MOD_ID + ":disable_experience", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, booleanValue) -> {
        RuneFeature.disableExperience = booleanValue.get();
        ClientboundDisableExperienceMessage.sync(booleanValue.get());
        server.getPlayerList().getPlayers().forEach(player -> updateFakeExperienceLevels(player, booleanValue.get()));
    }));
    public static final int FAKE_EXPERIENCE_LEVELS = 999999;
    //Players saved before the fake levels were increased have 9999 levels
    public static final int MIN_FAKE_EXPERIENCE_LEVELS = 9999;

    @Config
    public static Boolean mustLearnCurses = true;
    @Config(description = "Cumulative durability damage a player must deal/take while an item has an unlearned curse before they learn what that curse is. Some curses count durability damage more, and most are also revealed instantly when their effect shows up")
    public static Integer curseLearnThreshold = 100;
    @Config
    public static Boolean extractCurses = true;
    @Config(description = "If false, curses are disabled: they can't be applied, are never generated and the ones already on items have no effect and give no sockets.")
    public static Boolean enableCurses = true;
    @Config(description = "Maximum number of curses that can be on items that don't set it with the runeenchanting:curse_sockets item component (e.g. other mods' equipment).")
    public static Integer defaultCurseSockets = 1;
    @Config
    public static Double equipmentRuneConversionRatio = 0.5d;
    @Config(description = "Sockets given to the items in the runeenchanting:default_sockets item tag (e.g. other mods' tools, weapons and armor) that don't get them from data packs. Items in runeenchanting:no_default_sockets are excluded. 0 disables.")
    public static Integer defaultSockets = 4;

    public static Boolean disableExperience = true;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb)
                || !disableExperience)
            return;
        event.setCanceled(true);
    }

    ///Set once the mob's equipment has been converted, so the conversion isn't rolled again every time the mob is loaded
    private static final ResourceLocation NBT_EQUIPMENT_RUNES_ROLLED = RuneEnchanting.id("equipment_runes_rolled");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)
                || ModNBTData.contains(mob, NBT_EQUIPMENT_RUNES_ROLLED))
            return;
        ModNBTData.put(mob, NBT_EQUIPMENT_RUNES_ROLLED, true);
        var allRunes = RuneHooks.runeHolders();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isEmpty() || stack.has(REDataComponents.RUNES.get())) continue;
            var enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments == null || enchantments.isEmpty()) continue;
            int amount = MathHelper.getAmountWithDecimalChance(mob.level().getRandom(), enchantments.size() * equipmentRuneConversionRatio);
            if (disableExperience)
                stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            RuneHelper.addRandomRunes(stack, amount, mob.getRandom(), allRunes);
        }
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
        updateFakeExperienceLevels((ServerPlayer) event.getEntity(), disableExperience);
        CurseKnowledge.syncTeamOnLogin((ServerPlayer) event.getEntity());
        //These bonuses used to be saved on the player. If the runes are still active they are re-applied as non-permanent on the next check
        RERunes.MOONLIT.get().removeEffects(event.getEntity());
        RERunes.RETREAT.get().removeEffects(event.getEntity());
    }

    /**
     * Gives the fake levels when experience is disabled, otherwise removes them, leaving untouched players with real levels
     */
    private static void updateFakeExperienceLevels(ServerPlayer player, boolean disableExperience) {
        if (disableExperience)
            player.setExperienceLevels(FAKE_EXPERIENCE_LEVELS);
        else if (player.experienceLevel >= MIN_FAKE_EXPERIENCE_LEVELS)
            player.setExperienceLevels(0);
    }

    /**
     * NeoForge copies {@code copyOnDeath} attachments (like {@code SOULBOUND_ITEMS}) onto the new player via its own
     * {@code PlayerEvent.Clone} listener at {@code NORMAL} priority, so this must run afterward (LOW or lower) or
     * the stashed items may not have been copied to {@code event.getEntity()} yet.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;
        SoulboundItems.restore(event.getEntity());
    }

    /**
     * Programmatic item components provider giving {@link #defaultSockets} to the items in {@link REItemTagProvider#DEFAULT_SOCKETS}.
     * Data pack item component definitions are applied on top, so the items with sockets set by data packs keep them.
     * Items that already have sockets as default components (e.g. set by their own mod) are skipped.
     */
    public static void addDefaultSockets(RegistryAccess registryAccess, Map<Item, DataComponentPatch> patches) {
        if (defaultSockets <= 0)
            return;
        DataComponentPatch patch = DataComponentPatch.builder().set(REDataComponents.SOCKETS.get(), defaultSockets).build();
        for (Item item : BuiltInRegistries.ITEM) {
            Holder.Reference<Item> holder = item.builtInRegistryHolder();
            if (!holder.is(REItemTagProvider.DEFAULT_SOCKETS)
                    || holder.is(REItemTagProvider.NO_DEFAULT_SOCKETS)
                    || ItemComponentsReloadListener.getUnpatchedComponents(item).has(REDataComponents.SOCKETS.get()))
                continue;
            patches.put(item, patch);
        }
    }

    /**
     * Read before the levels are loaded, otherwise the experience orbs in the spawn chunks would be discarded using the previous world's (or the default) value.
     * The level's game rules aren't available yet, so they are read from the world data.
     */
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        disableExperience = event.getServer().getWorldData().getGameRules().getBoolean(RULE_DISABLEEXPERIENCE);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void removeExperienceBar(final RenderGuiLayerEvent.Pre event) {
        if (!disableExperience)
            return;

        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) || event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            event.setCanceled(true);
            if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
                LocalPlayer player = Minecraft.getInstance().player;
                //Skip the compensation if the jump meter is about to render in the same spot, otherwise hearts/food overlap it
                boolean jumpMeterWillRender = player != null && player.jumpableVehicle() != null && player.getJumpRidingScale() > 0;
                if (!jumpMeterWillRender) {
                    Minecraft.getInstance().gui.leftHeight -= 6;
                    Minecraft.getInstance().gui.rightHeight -= 6;
                }
            }
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        //Prevent merging a runed item into another one, as the result is a copy of the left item and the right item's runes (and curses) would be lost
        //Disabled runes are counted too, as they would be lost as well
        if (!event.getLeft().isEmpty()
                && !event.getRight().getOrDefault(REDataComponents.RUNES, List.of()).isEmpty()) {
            event.setCanceled(true);
            return;
        }
        if (event.getLeft().isEmpty() || !event.getRight().is(REItems.RUNE))
            return;
        ItemStack output = event.getLeft().copy();
        Holder<Rune> toApply = event.getRight().get(REDataComponents.STORED_RUNE.value());
        if (toApply == null
                || RuneHelper.canApplyRune(output, toApply) != RuneHelper.ApplyCheck.OK)
            return;
        if (!RuneHelper.addRune(output, toApply))
            return;
        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(0);
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
        if (event.getPlayer() instanceof ServerPlayer player)
            RECriteriaTriggers.RUNE_REMOVED.get().trigger(player);
        event.getContainerAccess().execute((world, pos) -> {
            Vec3 spawnPos = MCUtils.getGrindstoneOutputPos(world, pos);
            for (Holder<Rune> holder : removedRunes) {
                ItemStack runeStack = new ItemStack(REItems.RUNE, 1, DataComponentPatch.builder().set(REDataComponents.STORED_RUNE.value(), holder).build());
                ItemEntity itemEntity = new ItemEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, runeStack);
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
    public void onHurtItemStackCurseLearning(HurtItemStackEvent event) {
        if (!mustLearnCurses)
            return;
        ServerPlayer player = CurseKnowledge.getLearner(event.getLivingEntity());
        if (player == null)
            return;
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(event.getStack(), false);
        if (runes == null)
            return;
        for (Holder<Rune> holder : runes) {
            if (Rune.isCurse(holder))
                CurseKnowledge.addProgress(player, holder, event.getAmount() * holder.value().getCurseLearnMultiplier());
        }
    }

    ///Reveals Curse of Exposure when a mob starts targeting a player from farther than it could have without the curse
    @SubscribeEvent
    public void onChangeTargetExposure(LivingChangeTargetEvent event) {
        //Brain mobs (piglins, ...) use their own detection ranges instead of the follow range
        if (!mustLearnCurses
                || event.getTargetType() != LivingChangeTargetEvent.LivingTargetType.MOB_TARGET
                || !(event.getEntity() instanceof Mob mob)
                || !(event.getNewAboutToBeSetTarget() instanceof ServerPlayer player)
                || mob.getTarget() == player
                || mob.getLastHurtByMob() == player
                || CurseKnowledge.isLearned(player, RERunes.CURSE_OF_EXPOSURE)
                || !RuneHelper.hasRuneOnArmor(player, RERunes.CURSE_OF_EXPOSURE))
            return;
        AttributeInstance detectionRange = player.getAttribute(ILAttributes.MOB_DETECTION_RANGE);
        if (detectionRange == null)
            return;
        double totalDetectionRange = detectionRange.getValue();
        if (totalDetectionRange <= -1d)
            return;
        double visibilityWithoutCurse = player.getVisibilityPercent(mob) * (1d + totalDetectionRange - CurseOfExposureRune.detectionRangeIncrease) / (1d + totalDetectionRange);
        double rangeWithoutCurse = Math.max(mob.getAttributeValue(Attributes.FOLLOW_RANGE) * visibilityWithoutCurse, 2d);
        if (mob.distanceToSqr(player) > rangeWithoutCurse * rangeWithoutCurse)
            CurseKnowledge.reveal(player, RERunes.CURSE_OF_EXPOSURE);
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int sockets = RuneHelper.getSockets(stack);
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack, false, true);
        if (runes != null)
            runes = runes.stream()
                    .sorted(Comparator.<Holder<Rune>>comparingInt(h -> Rune.isCurse(h) ? 0 : 1)
                            .thenComparing(h -> h.value().getNameComponent().getString(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
        int runesCount = RuneHelper.countRunes(stack, false);
        int noCursesRunesCount = RuneHelper.countRunes(stack);
        boolean overloaded = RuneHelper.isOverloaded(stack);
        if (sockets > 0 && !stack.is(REItems.RUNE)) {
            if (runesCount > 0 || showExtraInfos(event.getFlags())) {
                event.getToolTip().add(CommonComponents.space());
                int curseSockets = RuneHelper.getCurseSockets(stack);
                Component socketsLine = curseSockets > 0
                        ? Component.translatable(RuneEnchanting.lang("sockets_with_curses"), noCursesRunesCount, sockets, RuneHelper.countCurses(stack), curseSockets)
                        : Component.translatable(RuneEnchanting.lang("sockets"), noCursesRunesCount, sockets);
                event.getToolTip().add(socketsLine.copy().withStyle(overloaded ? ChatFormatting.RED : ChatFormatting.DARK_PURPLE));
            }
        }
        Component inactiveTooltip = noCursesRunesCount > 0 ?RuneHelper.getInactiveConditionTooltip(stack) : null;
        if (overloaded)
            event.getToolTip().add(Component.translatable(RuneEnchanting.lang("overloaded_runes")).withStyle(ChatFormatting.RED));
        else if (inactiveTooltip != null)
            event.getToolTip().add(inactiveTooltip.copy().withStyle(ChatFormatting.RED));
        boolean inactive = overloaded || inactiveTooltip != null;
        if (runes != null) {
            Player player = event.getEntity();
            for (Holder<Rune> holder : runes) {
                boolean isCurse = Rune.isCurse(holder);
                ChatFormatting color = isCurse ? ChatFormatting.RED : inactive ? ChatFormatting.DARK_GRAY : ChatFormatting.LIGHT_PURPLE;
                if (isCurse && mustLearnCurses && !CurseKnowledge.isLearned(player, holder)) {
                    event.getToolTip().add(CommonComponents.space().append(getTooltipName(holder, Component.translatable(RuneEnchanting.lang("unknown_curse")), color)));
                    continue;
                }
                event.getToolTip().add(CommonComponents.space().append(getTooltipName(holder, holder.value().getNameComponent(), color)));
                if (showExtraInfos(event.getFlags())) {
                    event.getToolTip().add(CommonComponents.space().append(holder.value().getDescriptionComponent()).withStyle(ChatFormatting.GRAY));
                    if (isCurse)
                        event.getToolTip().add(CommonComponents.space().append(Component.translatable(RuneEnchanting.lang("cursed_info"))).withStyle(ChatFormatting.GRAY));
                }
                holder.value().addTooltip(stack, event.getToolTip(), event.getFlags());
            }
        }
    }
    
    /**
     * Fires after vanilla has already computed the final knockback strength (e.g. including the sprint-attack
     * bonus), unlike {@code EnchantmentHelper.modifyKnockback}, so this is where rune effects that need to see
     * (and change) the knockback the player actually feels for melee hits must hook in.
     */
    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level))
            return;
        LivingEntity attacker = CurrentAttacker.resolve(event.getEntity());
        if (attacker == null)
            return;
        ItemStack weapon = attacker.getMainHandItem();
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(weapon);
        if (runes == null)
            return;
        float knockback = event.getStrength();
        for (Holder<Rune> holder : runes) {
            knockback = holder.value().modifyMeleeKnockback(level, weapon, attacker, event.getEntity(), knockback);
        }
        event.setStrength(knockback);
    }

    @SubscribeEvent
    public void onGatherSkippedAttributeTooltips(GatherSkippedAttributeTooltipsEvent event) {
        if (!mustLearnCurses)
            return;
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(event.getStack(), false);
        if (runes == null)
            return;
        Player player = event.getContext().player();
        for (Holder<Rune> holder : runes) {
            if (Rune.isCurse(holder) && !CurseKnowledge.isLearned(player, holder))
                event.skipId(RERunes.REGISTRY.getKey(holder.value()));
        }
    }

    /**
     * Name of the rune as shown in tooltips: curses get the glitch effect
     */
    private static Component getTooltipName(Holder<Rune> rune, Component name, ChatFormatting color) {
        if (Rune.isCurse(rune) && RuneFeatureClient.glitchCurseNames)
            return CurseGlitch.corrupt(name, color);
        return name.copy().withStyle(color);
    }

    public static boolean showExtraInfos(TooltipFlag flag) {
        return flag.hasShiftDown() || RuneFeatureClient.alwaysShowExtraInfos;
    }

}
