package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.REMobEffects;
import insane96mcp.runeenchanting.setup.RERunes;
import insane96mcp.runeenchanting.setup.RESoundEvents;
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

        add("entity.runeenchanting.shard", "Shard");

        for (RESoundEvents.SoundInfo sound : RESoundEvents.SOUNDS)
            add(sound.subtitleKey(), sound.subtitle());

        add(REMobEffects.MINING_MOMENTUM.get(), "Momentum (mining)");
        add(REMobEffects.MOVEMENT_MOMENTUM.get(), "Momentum (moving)");
        add(REMobEffects.ATTACK_MOMENTUM.get(), "Momentum (attacking)");

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
        add("advancements.runeenchanting.rune_removed.description", "Remove a rune from an item");
        add("advancements.runeenchanting.runic_table.title", "Rune Library");
        add("advancements.runeenchanting.runic_table.description", "Craft a Runic Table to store your runes and apply them to items");
        add("advancements.runeenchanting.curse_learned.title", "Curse Revealed");
        add("advancements.runeenchanting.curse_learned.description", "Use a cursed item enough to discover its curse");

        //EMI info pages, in assets/emi/recipe/additions/
        add("emi.info.runeenchanting.rune", "Runes replace enchanting. Each Rune gives an item an effect, like an enchantment.");
        add("emi.info.runeenchanting.rune.sources", "Enchanted books and enchanted items found in chests turn into Runes. Some structures hide their own kinds of Runes, Bottles o' Enchanting in chests may turn into Runes, and Spawners drop several Runes when broken.");
        add("emi.info.runeenchanting.rune.apply", "To apply a Rune, combine the item with it in an Anvil, or use a Runic Table. Each item has a limited number of Sockets, shown in its tooltip. A Grindstone takes all the Runes off an item and gives them back.");
        add("emi.info.runeenchanting.rune.curses", "Some Runes are Curses, and you only find out which one after using the item for a while. A Curse gives the item an extra Socket.");
        add("emi.info.runeenchanting.encased_rune", "Turns two Runes you don't need into a random new one. Smelting it in a Furnace takes a long time.");
        add("emi.info.runeenchanting.runic_table", "Stores any number of Runes, shared by everyone who uses it. Put an item in its slot to see which stored Runes can go on it, then apply or remove them. Breaking the table drops all the stored Runes.");
        add("emi.info.runeenchanting.sculk", "Only drops itself when mined with Silk Touch. Otherwise, very rarely, it drops a random Rune, which may be a Curse.");
    }
}
