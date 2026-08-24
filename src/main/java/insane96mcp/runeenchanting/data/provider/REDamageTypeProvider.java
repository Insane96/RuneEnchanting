package insane96mcp.runeenchanting.data.provider;

import insane96mcp.runeenchanting.RuneEnchanting;
import insane96mcp.runeenchanting.runes.BloodFueledRune;
import insane96mcp.runeenchanting.runes.ElectrocutionRune;
import insane96mcp.runeenchanting.runes.curse.CurseOfBloodPactRune;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class REDamageTypeProvider {

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BloodFueledRune.DAMAGE_TYPE, new DamageType(RuneEnchanting.lang("blood_fueled"), DamageScaling.NEVER, 0.0F));
        context.register(CurseOfBloodPactRune.DAMAGE_TYPE, new DamageType(RuneEnchanting.lang("blood_pact"), DamageScaling.NEVER, 0.0F));
        context.register(ElectrocutionRune.DAMAGE_TYPE, new DamageType(RuneEnchanting.lang("electrocution"), DamageScaling.NEVER, 0.0F));
    }
}
