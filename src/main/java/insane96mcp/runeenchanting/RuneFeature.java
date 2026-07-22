package insane96mcp.runeenchanting;

import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.Module;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.insanelib.util.IntegratedPack;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.runeenchanting.network.message.ClientboundDisableExperienceMessage;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.REItems;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.Comparator;
import java.util.List;

@LoadFeature(canBeDisabled = false)
public class RuneFeature extends Feature {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLEEXPERIENCE = GameRules.register(RuneEnchanting.MOD_ID + ":disable_experience", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, booleanValue) -> {
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
    @Config(description = "Maximum number of curses that can be on a single item. 0 disables curses entirely.")
    public static Integer maxCurses = 1;
    @Config(description = """
            A data pack will be enabled that changes the following:
            * Removes the enchanting table recipe
            * Changes Channeling enchantment (and subsequently the Channeling rune) to work in rain too""")
    public static Boolean integratedDataPack = true;
    @Config(description = "If true, enchanting related items are hidden from creative inventory")
    public static Boolean hideEnchantingRelatedItems = true;

    @Config
    public static Double equipmentRuneConversionRatio = 0.5d;

    @Config(description = "If true, infos that are usually shown only with shift held down, will always show instead")
    public static Boolean alwaysShowExtraInfos = false;

    public static Boolean disableExperience = true;

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob))
            return;
        var allRunes = event.getLevel().registryAccess().registryOrThrow(RERunes.REGISTRY_KEY).holders().toList();
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
        if (disableExperience)
            ((ServerPlayer) event.getEntity()).setExperienceLevels(9999);
        else
            ((ServerPlayer) event.getEntity()).setExperienceLevels(0);
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        disableExperience = event.getServer().getGameRules().getBoolean(RULE_DISABLEEXPERIENCE);
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
                || !toApply.value().canBeAppliedTo(output)
                || (Rune.isCurse(toApply) && RuneHelper.countCurses(output) >= maxCurses))
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
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int sockets = RuneHelper.getSockets(stack);
        List<Holder<Rune>> runes = RuneHelper.getRunesByPriority(stack, false);
        if (runes != null)
            runes = runes.stream().sorted(Comparator.comparingInt(h -> (Rune.isCurse(h) ? 1 : 0))).toList();
        int runesCount = RuneHelper.countRunes(stack, false);
        int noCursesRunesCount = RuneHelper.countRunes(stack);
        if (sockets > 0 && !stack.is(REItems.RUNE)) {
            if (runesCount > 0 || showExtraInfos(event.getFlags())) {
                event.getToolTip().add(CommonComponents.space());
                event.getToolTip().add(Component.translatable("sockets", noCursesRunesCount, sockets).withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
        if (runes != null) {
            boolean isCursed = false;
            for (Holder<Rune> holder : runes) {
                if (Rune.isCurse(holder) && hideCurses) {
                    isCursed = true;
                    continue;
                }
                ChatFormatting color = ChatFormatting.LIGHT_PURPLE;
                if (Rune.isCurse(holder))
                    color = ChatFormatting.RED;
                event.getToolTip().add(CommonComponents.space().append(holder.value().getNameComponent().withStyle(color)));
                if (showExtraInfos(event.getFlags())) {
                    event.getToolTip().add(CommonComponents.space().append(holder.value().getDescriptionComponent()).withStyle(ChatFormatting.GRAY));
                    if (Rune.isCurse(holder))
                        event.getToolTip().add(CommonComponents.space().append(Component.translatable("cursed_info")).withStyle(ChatFormatting.GRAY));
                }
                holder.value().addTooltip(stack, event.getToolTip(), event.getFlags());
            }
            if (isCursed) {
                if (stack.is(REItems.RUNE))
                    event.getToolTip().add(CommonComponents.space().append(Component.translatable("curse").withStyle(ChatFormatting.RED)));
                else
                    event.getToolTip().add(CommonComponents.space().append(Component.translatable("cursed").withStyle(ChatFormatting.RED)));
            }
        }
    }
    
    public static boolean showExtraInfos(TooltipFlag flag) {
        return flag.hasShiftDown() || alwaysShowExtraInfos;
    }

}
