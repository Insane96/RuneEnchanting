package insane96mcp.runeenchanting.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.mixin.FishingHookAccessor;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JuicyBaitRune extends Rune {
    @Config
    public static Double extraFishChance = 0.15d;

    @Override
    public String getName() {
        return "Juicy Bait";
    }

    @Override
    public String getDescription() {
        return "Chance to fish 2 items instead of 1";
    }

    @Override
    public @Nullable String getInfo() {
        return "Chance for double item: %s%%";
    }

    @Override
    public void addItemsToApplicableTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> appender) {
        appender.add(Items.FISHING_ROD);
    }

    @Override
    public void onItemFished(ItemFishedEvent event, ItemStack stack) {
        Player player = event.getEntity();
        if (player.getRandom().nextDouble() > extraFishChance)
            return;
        boolean hasFishingRod = player.getMainHandItem().getItem() instanceof FishingRodItem;
        boolean hasFishingRodInOffHand = player.getOffhandItem().getItem() instanceof FishingRodItem;
        if (!hasFishingRod && !hasFishingRodInOffHand)
            return;
        FishingHook hookEntity = event.getHookEntity();
        LootParams lootparams = (new LootParams.Builder((ServerLevel) player.level())).withParameter(LootContextParams.ORIGIN, hookEntity.position()).withParameter(LootContextParams.TOOL, stack).withParameter(LootContextParams.THIS_ENTITY, hookEntity).withParameter(LootContextParams.ATTACKING_ENTITY, player).withParameter(LootContextParams.THIS_ENTITY, hookEntity).withLuck((float) ((FishingHookAccessor) hookEntity).getLuck() + player.getLuck()).create(LootContextParamSets.FISHING);
        LootTable loottable = player.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
        List<ItemStack> list = loottable.getRandomItems(lootparams);
        for(ItemStack itemstack : list) {
            ItemEntity itementity = new ItemEntity(hookEntity.level(), hookEntity.getX(), hookEntity.getY(), hookEntity.getZ(), itemstack);
            double xDiff = player.getX() - hookEntity.getX();
            double yDiff = player.getY() - hookEntity.getY();
            double zDiff = player.getZ() - hookEntity.getZ();
            double deltaMovMultiplier = 0.1D;
            itementity.setDeltaMovement(xDiff * deltaMovMultiplier, yDiff * deltaMovMultiplier + Math.sqrt(Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff)) * 0.08D, zDiff * deltaMovMultiplier);
            hookEntity.level().addFreshEntity(itementity);
            player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, event.getEntity().getRandom().nextInt(6) + 1));
            if (itemstack.is(ItemTags.FISHES))
                player.awardStat(Stats.FISH_CAUGHT, 1);
        }
        this.onItemFished(event, stack);
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), RuneEnchanting.NO_DECIMAL_FORMATTER.format(extraFishChance * 100));
    }
}
