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

        add("death.attack.runeenchanting.blood_fueled", "%1$s's blood-fueled rune drained too much life essence");
        add("death.attack.runeenchanting.blood_pact", "%1$s's blood pact was fulfilled");

        add("item.runeenchanting.rune", "Rune");
        add("item.runeenchanting.encased_rune", "Encased Rune");
        add(RuneEnchanting.lang("encased_rune.tooltip"), "Smelt to get a random rune");
        add("sockets", "Sockets: %d/%d");
        add("unknown_curse", "Unknown Curse");
        add("cursed_info", "+1 Socket");
        add("curse_learned", "You have learned about %s");
    }
}
