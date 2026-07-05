package insane96mcp.runeenchanting;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
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
}
