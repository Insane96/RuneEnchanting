package insane96mcp.runeenchanting.setup;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class REMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, RuneEnchanting.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RunicTableMenu>> RUNIC_TABLE =
            REGISTRY.register("runic_table", () -> new MenuType<>(RunicTableMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
