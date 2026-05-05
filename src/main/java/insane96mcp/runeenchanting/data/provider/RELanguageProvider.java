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

        add("item.runeenchanting.rune", "Rune");
        add("sockets", "Sockets: %d/%d");
        add("curse", "Curse");
        add("cursed", "Cursed");
        add("cursed_info", "+1 Socket");
    }
}
