package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RELanguageProvider extends LanguageProvider {

    public RELanguageProvider(PackOutput output) {
        super(output, RuneEnchanting.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var entry : RERunes.REGISTRY.entrySet()) {
            var rune = entry.getValue();
            add(rune.getNameTranslationKey(), rune.getName());
            add(rune.getDescriptionTranslationKey(), rune.getDescription());
            if (rune.getInfo() != null)
                add(rune.getInfoTranslationKey(), rune.getInfo());
        }

        add("death.attack.runeenchanting.blood_fueled", "%1$s's was consumed by blood-fueled rune");
        add("death.attack.runeenchanting.blood_pact", "%1$s's was consumed by the blood pact");
        add("death.attack.runeenchanting.electrocution", "%1$s was electrocuted");

        add("item.runeenchanting.rune", "Rune");
        add("item.runeenchanting.encased_rune", "Encased Rune");
        add("block.runeenchanting.runic_table", "Runic Table");
        add("container.runeenchanting.runic_table", "Runic Table");
        add("container.runeenchanting.runic_table.sockets", "Sockets: %s/%s");
        add("container.runeenchanting.runic_table.search", "Search...");
        add("container.runeenchanting.runic_table.no_runes", "No runes found");
        add("container.runeenchanting.runic_table.empty","Click here with runes or shift click them to store them");
        add("container.runeenchanting.runic_table.hint.insert", "Left click: store all");
        add("container.runeenchanting.runic_table.hint.insert_one", "Right click: store one");
        add("container.runeenchanting.runic_table.hint.apply", "Left click: apply to the item");
        add("container.runeenchanting.runic_table.hint.extract", "Right click: take one");
        add("container.runeenchanting.runic_table.hint.extract_stack", "Shift + right click: take a stack");
        add("container.runeenchanting.runic_table.hint.remove", "Click the x to remove it");
        add("container.runeenchanting.runic_table.reason.disabled", "This rune is disabled");
        add("container.runeenchanting.runic_table.reason.not_applicable", "Can't be applied to this item");
        add("container.runeenchanting.runic_table.reason.already_present", "The item already has this rune");
        add("container.runeenchanting.runic_table.reason.no_free_sockets", "No free sockets");
        add("container.runeenchanting.runic_table.reason.too_many_curses", "The item can't have more curses");
        add("container.runeenchanting.runic_table.reason.not_present", "The item doesn't have this rune");
        add("container.runeenchanting.runic_table.reason.curse_locked", "Curses can't be removed");
        add("container.runeenchanting.runic_table.reason.would_overload", "Removing this curse would leave the item with too many runes");
        add(RuneEnchanting.lang("encased_rune.tooltip"), "Smelt to get a random rune");
        add(RuneEnchanting.lang("sockets"), "Sockets: %d/%d");
        add(RuneEnchanting.lang("overloaded_runes"), "Too many runes: runes are inactive. Remove them with a grindstone.");
        add(RuneEnchanting.lang("unknown_curse"), "Unknown Curse");
        add(RuneEnchanting.lang("cursed_info"), "+1 Socket");
        add(RuneEnchanting.lang("curse_learned"), "You have learned about %s");

        add("advancements.runeenchanting.first_rune.title", "First Rune");
        add("advancements.runeenchanting.first_rune.description", "Obtain a rune");
        add("advancements.runeenchanting.rune_removed.title", "Clean Slate");
        add("advancements.runeenchanting.rune_removed.description", "Remove a rune from an item with a grindstone");
        add("advancements.runeenchanting.curse_learned.title", "Curse Revealed");
        add("advancements.runeenchanting.curse_learned.description", "Use a cursed item enough to discover its curse");
    }
}
