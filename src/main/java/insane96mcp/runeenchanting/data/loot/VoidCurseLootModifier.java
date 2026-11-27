package insane96mcp.runeenchanting.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import insane96mcp.runeenchanting.CurseKnowledge;
import insane96mcp.runeenchanting.RuneHelper;
import insane96mcp.runeenchanting.runes.curse.CurseOfTheVoidRune;
import insane96mcp.runeenchanting.setup.RERunes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class VoidCurseLootModifier extends LootModifier {
    public static final MapCodec<VoidCurseLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, VoidCurseLootModifier::new));

    public VoidCurseLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool == null || tool.isEmpty())
            return generatedLoot;
        if (!RuneHelper.hasRune(tool, RERunes.CURSE_OF_THE_VOID))
            return generatedLoot;
        if (generatedLoot.isEmpty()
                || context.getRandom().nextFloat() >= CurseOfTheVoidRune.chanceToVoid)
            return generatedLoot;
        generatedLoot.clear();
        //Mined blocks have the player as this entity, fishing has the hook
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof FishingHook hook)
            entity = hook.getPlayerOwner();
        CurseKnowledge.reveal(entity, RERunes.CURSE_OF_THE_VOID);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
