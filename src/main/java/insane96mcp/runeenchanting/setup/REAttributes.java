package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REAttributes {
    public static final DeferredRegister<Attribute> REGISTRY =
            DeferredRegister.create(Registries.ATTRIBUTE, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> OFF_GROUND_MINING_SPEED = REGISTRY.register(
            "off_ground_mining_speed",
            () -> new RangedAttribute("attribute.runeenchanting.off_ground_mining_speed", 0.2, 0.0, 1.0).setSyncable(true)
    );
}
