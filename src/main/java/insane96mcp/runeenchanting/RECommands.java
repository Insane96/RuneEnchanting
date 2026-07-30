package insane96mcp.runeenchanting;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RECommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("rune")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                    .then(Commands.argument("rune", ResourceLocationArgument.id())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                        .executes(RECommands::addRune)))
                .then(Commands.literal("remove")
                    .then(Commands.argument("rune", ResourceLocationArgument.id())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                        .executes(RECommands::removeRune)))
                .then(Commands.literal("clear")
                    .executes(RECommands::clearRunes))
                .then(Commands.literal("get_random_rune")
                    .executes(ctx -> getRandomRuneItem(ctx, null))
                    .then(Commands.argument("tag", ResourceLocationArgument.id())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                            RERunes.REGISTRY.getTagNames().map(TagKey::location).toList(), builder))
                        .executes(ctx -> getRandomRuneItem(ctx, ResourceLocationArgument.getId(ctx, "tag")))))
                .then(Commands.literal("fix_enchantments_component")
                    .executes(RECommands::fixEnchantmentsComponent))
                .then(Commands.literal("curse")
                    .then(Commands.literal("learn")
                        .then(Commands.argument("rune", ResourceLocationArgument.id())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                            .executes(ctx -> learnCurse(ctx, null))
                            .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> learnCurse(ctx, EntityArgument.getPlayer(ctx, "target"))))))
                    .then(Commands.literal("forget")
                        .then(Commands.argument("rune", ResourceLocationArgument.id())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                            .executes(ctx -> forgetCurse(ctx, null))
                            .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> forgetCurse(ctx, EntityArgument.getPlayer(ctx, "target"))))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> resetCurses(ctx, null))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> resetCurses(ctx, EntityArgument.getPlayer(ctx, "target")))))
                    .then(Commands.literal("progress")
                        .then(Commands.literal("set")
                            .then(Commands.argument("rune", ResourceLocationArgument.id())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                    .executes(ctx -> setCurseProgress(ctx, IntegerArgumentType.getInteger(ctx, "amount"), null))
                                    .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> setCurseProgress(ctx, IntegerArgumentType.getInteger(ctx, "amount"), EntityArgument.getPlayer(ctx, "target")))))))
                        .then(Commands.literal("add")
                            .then(Commands.argument("rune", ResourceLocationArgument.id())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(RERunes.REGISTRY.keySet(), builder))
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                    .executes(ctx -> addCurseProgress(ctx, IntegerArgumentType.getInteger(ctx, "amount"), null))
                                    .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> addCurseProgress(ctx, IntegerArgumentType.getInteger(ctx, "amount"), EntityArgument.getPlayer(ctx, "target"))))))))
                    .then(Commands.literal("list")
                        .executes(ctx -> listCurseKnowledge(ctx, null))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> listCurseKnowledge(ctx, EntityArgument.getPlayer(ctx, "target"))))))
        );
    }

    private static int addRune(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No item in main hand"));
            return 0;
        }
        ResourceLocation runeId = ResourceLocationArgument.getId(ctx, "rune");
        Optional<Holder.Reference<Rune>> holderOpt = RERunes.REGISTRY.getHolder(ResourceKey.create(RERunes.REGISTRY_KEY, runeId));
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + runeId));
            return 0;
        }
        Holder<Rune> runeHolder = holderOpt.get();
        if (!RuneHelper.addRune(stack, runeHolder)) {
            ctx.getSource().sendFailure(Component.literal("Sockets full or or item already has ").append(runeHolder.value().getNameComponent()));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Added ").append(runeHolder.value().getNameComponent()), false);
        return 1;
    }

    private static int removeRune(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No item in main hand"));
            return 0;
        }
        ResourceLocation runeId = ResourceLocationArgument.getId(ctx, "rune");
        Optional<Holder.Reference<Rune>> holderOpt = RERunes.REGISTRY.getHolder(ResourceKey.create(RERunes.REGISTRY_KEY, runeId));
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + runeId));
            return 0;
        }
        Holder<Rune> runeHolder = holderOpt.get();
        if (!RuneHelper.removeRune(stack, runeHolder)) {
            ctx.getSource().sendFailure(Component.literal("Item does not have ").append(runeHolder.value().getNameComponent()));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Removed ").append(runeHolder.value().getNameComponent()), false);
        return 1;
    }

    private static int clearRunes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No item in main hand"));
            return 0;
        }
        RuneHelper.clearRunes(stack, true);
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared all runes"), false);
        return 1;
    }

    private static int fixEnchantmentsComponent(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No item in main hand"));
            return 0;
        }
        if (stack.has(DataComponents.ENCHANTMENTS)) {
            ctx.getSource().sendFailure(Component.literal("Item already has the enchantments component"));
            return 0;
        }
        stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ctx.getSource().sendSuccess(() -> Component.literal("Restored the enchantments component on the held item"), false);
        return 1;
    }

    private static int getRandomRuneItem(CommandContext<CommandSourceStack> ctx, @Nullable ResourceLocation tagId) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();

        List<? extends Holder<Rune>> pool;
        if (tagId == null) {
            pool = RERunes.REGISTRY.holders().collect(Collectors.toList());
        } else {
            TagKey<Rune> tagKey = TagKey.create(RERunes.REGISTRY_KEY, tagId);
            Optional<HolderSet.Named<Rune>> tagOpt = RERunes.REGISTRY.getTag(tagKey);
            if (tagOpt.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("Unknown rune tag: " + tagId));
                return 0;
            }
            pool = tagOpt.get().stream().collect(Collectors.toList());
        }

        ItemStack runeItem = RuneHelper.createRandomRuneItem(ctx.getSource().getLevel().getRandom(), pool);
        if (runeItem.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No eligible runes found"));
            return 0;
        }
        player.addItem(runeItem);
        Holder<Rune> stored = runeItem.get(REDataComponents.STORED_RUNE);
        ctx.getSource().sendSuccess(() -> Component.literal("Given rune item: ").append(stored != null ? stored.value().getNameComponent() : runeItem.getDisplayName()), false);
        return 1;
    }

    private static ServerPlayer resolveTarget(CommandContext<CommandSourceStack> ctx, @Nullable ServerPlayer target) throws CommandSyntaxException {
        return target != null ? target : ctx.getSource().getPlayerOrException();
    }

    private static Optional<Holder.Reference<Rune>> getRuneHolder(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation runeId = ResourceLocationArgument.getId(ctx, "rune");
        return RERunes.REGISTRY.getHolder(ResourceKey.create(RERunes.REGISTRY_KEY, runeId));
    }

    private static String progressDescription(ServerPlayer player, Holder<Rune> curse) {
        if (CurseKnowledge.isLearned(player, curse))
            return "learned";
        return CurseKnowledge.getProgress(player, curse) + "/" + RuneFeature.curseLearnThreshold;
    }

    private static int learnCurse(CommandContext<CommandSourceStack> ctx, @Nullable ServerPlayer target) throws CommandSyntaxException {
        Optional<Holder.Reference<Rune>> holderOpt = getRuneHolder(ctx);
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + ResourceLocationArgument.getId(ctx, "rune")));
            return 0;
        }
        Holder<Rune> curse = holderOpt.get();
        if (!Rune.isCurse(curse)) {
            ctx.getSource().sendFailure(curse.value().getNameComponent().copy().append(" is not a curse"));
            return 0;
        }
        ServerPlayer player = resolveTarget(ctx, target);
        CurseKnowledge.learn(player, curse);
        ctx.getSource().sendSuccess(() -> Component.literal(player.getGameProfile().getName() + " learned ").append(curse.value().getNameComponent()), false);
        return 1;
    }

    private static int forgetCurse(CommandContext<CommandSourceStack> ctx, @Nullable ServerPlayer target) throws CommandSyntaxException {
        Optional<Holder.Reference<Rune>> holderOpt = getRuneHolder(ctx);
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + ResourceLocationArgument.getId(ctx, "rune")));
            return 0;
        }
        Holder<Rune> curse = holderOpt.get();
        ServerPlayer player = resolveTarget(ctx, target);
        if (!CurseKnowledge.forget(player, curse)) {
            ctx.getSource().sendFailure(Component.literal(player.getGameProfile().getName() + " hadn't learned ").append(curse.value().getNameComponent()));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal(player.getGameProfile().getName() + " forgot ").append(curse.value().getNameComponent()), false);
        return 1;
    }

    private static int resetCurses(CommandContext<CommandSourceStack> ctx, @Nullable ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolveTarget(ctx, target);
        CurseKnowledge.resetAll(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Reset curse knowledge for " + player.getGameProfile().getName()), false);
        return 1;
    }

    private static int setCurseProgress(CommandContext<CommandSourceStack> ctx, int amount, @Nullable ServerPlayer target) throws CommandSyntaxException {
        Optional<Holder.Reference<Rune>> holderOpt = getRuneHolder(ctx);
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + ResourceLocationArgument.getId(ctx, "rune")));
            return 0;
        }
        Holder<Rune> curse = holderOpt.get();
        if (!Rune.isCurse(curse)) {
            ctx.getSource().sendFailure(curse.value().getNameComponent().copy().append(" is not a curse"));
            return 0;
        }
        ServerPlayer player = resolveTarget(ctx, target);
        CurseKnowledge.setProgress(player, curse, amount);
        ctx.getSource().sendSuccess(() -> Component.literal(player.getGameProfile().getName() + "'s progress for ").append(curse.value().getNameComponent()).append(Component.literal(": " + progressDescription(player, curse))), false);
        return 1;
    }

    private static int addCurseProgress(CommandContext<CommandSourceStack> ctx, int amount, @Nullable ServerPlayer target) throws CommandSyntaxException {
        Optional<Holder.Reference<Rune>> holderOpt = getRuneHolder(ctx);
        if (holderOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Unknown rune: " + ResourceLocationArgument.getId(ctx, "rune")));
            return 0;
        }
        Holder<Rune> curse = holderOpt.get();
        if (!Rune.isCurse(curse)) {
            ctx.getSource().sendFailure(curse.value().getNameComponent().copy().append(" is not a curse"));
            return 0;
        }
        ServerPlayer player = resolveTarget(ctx, target);
        CurseKnowledge.addProgress(player, curse, amount);
        ctx.getSource().sendSuccess(() -> Component.literal(player.getGameProfile().getName() + "'s progress for ").append(curse.value().getNameComponent()).append(Component.literal(": " + progressDescription(player, curse))), false);
        return 1;
    }

    private static int listCurseKnowledge(CommandContext<CommandSourceStack> ctx, @Nullable ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolveTarget(ctx, target);
        List<Holder<Rune>> learned = CurseKnowledge.getLearned(player);
        MutableComponent message = Component.literal(player.getGameProfile().getName() + " has learned " + learned.size() + " curse(s)");
        for (Holder<Rune> curse : learned) {
            message.append("\n - ").append(curse.value().getNameComponent());
        }
        Map<Holder<Rune>, Integer> progress = CurseKnowledge.getProgressMap(player);
        if (!progress.isEmpty()) {
            message.append("\nIn progress:");
            for (Map.Entry<Holder<Rune>, Integer> entry : progress.entrySet()) {
                message.append("\n - ").append(entry.getKey().value().getNameComponent())
                        .append(Component.literal(": " + entry.getValue() + "/" + RuneFeature.curseLearnThreshold));
            }
        }
        ctx.getSource().sendSuccess(() -> message, false);
        return 1;
    }
}
