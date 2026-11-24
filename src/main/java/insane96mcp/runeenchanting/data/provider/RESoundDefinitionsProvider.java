package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.setup.RESoundEvents;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class RESoundDefinitionsProvider extends SoundDefinitionsProvider {
    public RESoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, RuneEnchanting.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        // Each sound plays the vanilla sound event (with all its variations) but has its own subtitle
        for (RESoundEvents.SoundInfo info : RESoundEvents.SOUNDS) {
            add(info.sound(), SoundDefinition.definition()
                    .subtitle(info.subtitleKey())
                    .with(SoundDefinition.Sound.sound(info.vanillaSound(), SoundDefinition.SoundType.EVENT)));
        }
    }
}
