package insane96mcp.runeenchanting.runes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class PartBreakerRune extends Rune {

    @Override
    public String getName() {
        return "Part Breaker";
    }

    @Override
    public String getDescription() {
        return "Drops extra items from special loot tables on kill";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .add(Items.TRIDENT);
    }

    @Override
    public void onLivingDrops(LivingDropsEvent event, ItemStack stack) {
        if (!(event.getEntity().level() instanceof ServerLevel level))
            return;

        ResourceLocation entityTypeId = event.getEntity().getType().builtInRegistryHolder().key().location();
        ResourceLocation lootTableLocation = ResourceLocation.fromNamespaceAndPath(
                entityTypeId.getNamespace(), "part_breaking/" + entityTypeId.getPath());
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableLocation));
        if (lootTable == LootTable.EMPTY)
            return;

        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, event.getEntity())
                .withParameter(LootContextParams.ORIGIN, event.getEntity().position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, event.getSource().getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, event.getSource().getDirectEntity());
        if (event.getSource().getEntity() instanceof Player player)
            paramsBuilder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);

        LootParams lootParams = paramsBuilder.create(LootContextParamSets.ENTITY);
        lootTable.getRandomItems(lootParams).forEach(bonusStack ->
                event.getDrops().add(new ItemEntity(level,
                        event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), bonusStack)));
    }
}
