package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.entity.ShardProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REEntityTypes {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.ENTITY_TYPE, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ShardProjectile>> SHARD =
            REGISTRY.register("shard", () -> EntityType.Builder.<ShardProjectile>of(ShardProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(RuneEnchanting.MOD_ID + ":shard"));
}
