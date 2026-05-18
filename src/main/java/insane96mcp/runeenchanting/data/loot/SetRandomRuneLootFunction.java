package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RELootFunctions;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetRandomRuneLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetRandomRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).and(
                    Codec.BOOL.optionalFieldOf("allow_curses", true).forGetter(f -> f.allowCurses)
            ).apply(inst, SetRandomRuneLootFunction::new));

    private final boolean allowCurses;

    public SetRandomRuneLootFunction(List<LootItemCondition> conditions, boolean allowCurses) {
        super(conditions);
        this.allowCurses = allowCurses;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        var registry = context.getLevel().registryAccess().registryOrThrow(RERunes.REGISTRY_KEY);
        List<Holder<Rune>> eligible = registry.holders()
                .filter(h -> h.value().isEnabled() && (allowCurses || !Rune.isCurse(h)))
                .collect(Collectors.toCollection(ArrayList::new));
        if (eligible.isEmpty())
            return stack;
        Holder<Rune> picked = eligible.get(context.getRandom().nextInt(eligible.size()));
        stack.set(REDataComponents.STORED_RUNE, picked);
        return stack;
    }

    @Override
    public LootItemFunctionType<SetRandomRuneLootFunction> getType() {
        return RELootFunctions.SET_RANDOM_RUNE.get();
    }
}
