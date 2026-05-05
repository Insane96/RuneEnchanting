package insane96mcp.runeenchanting;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

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
}
