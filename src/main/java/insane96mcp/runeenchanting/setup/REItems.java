package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REItems {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(Registries.ITEM, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<Item, Item> RUNE =
            REGISTRY.register("rune", () -> new RuneItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ENCASED_RUNE =
            REGISTRY.register("encased_rune", () -> new EncasedRuneItem(new Item.Properties()));
}
