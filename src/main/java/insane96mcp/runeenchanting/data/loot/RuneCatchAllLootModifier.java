package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.runeenchanting.RuneFeature;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.ArrayList;
import java.util.List;

public class RuneCatchAllLootModifier extends LootModifier {
    public static final MapCodec<RuneCatchAllLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, RuneCatchAllLootModifier::new));

    public RuneCatchAllLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        List<? extends Holder<Rune>> allRunes = context.getLevel().registryAccess()
                .registryOrThrow(RERunes.REGISTRY_KEY)
                .holders()
                .toList();

        List<ItemStack> toAdd = new ArrayList<>();
        List<ItemStack> toRemove = new ArrayList<>();

        for (ItemStack stack : generatedLoot) {
            if (stack.has(REDataComponents.RUNES.get()))
                continue;

            var enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null && !enchantments.isEmpty()) {
                if (RuneFeature.disableExperience)
                    stack.remove(DataComponents.ENCHANTMENTS);
                List<? extends Holder<Rune>> compatibleRunes = allRunes.stream()
                        .filter(h -> h.value().isEnabled() && h.value().canBeAppliedTo(stack))
                        .toList();
                RuneHelper.addRandomRunes(stack, enchantments.size(), context.getRandom(), compatibleRunes.isEmpty() ? allRunes : compatibleRunes);
            }
            else if (stack.is(Items.ENCHANTED_BOOK)) {
                var stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
                if (stored != null && !stored.isEmpty()) {
                    ItemStack runeItem = RuneHelper.createRandomRuneItem(context.getRandom(), allRunes);
                    if (!runeItem.isEmpty())
                        toAdd.add(runeItem);
                    if (RuneFeature.disableExperience)
                        toRemove.add(stack);
                }
            }
        }

        generatedLoot.addAll(toAdd);
        toRemove.forEach(generatedLoot::remove);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
