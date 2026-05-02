package insane96mcp.runeenchanting.data.runes;

import insane96mcp.insanelib.core.feature.config.Config;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

public class ProtectionRune extends DamageReductionRune {
    @Config
    public static Double damageReduction = 0.06d;

    @Override
    public String getName() {
        return "Protection";
    }

    @Override
    public String getDescription() {
        return "Reduces damage taken";
    }

    @Override
    public float damageReduction() {
        return damageReduction.floatValue();
    }

    @Override
    public @Nullable String getInfo() {
        return "Damage reduction: %s%%";
    }

    @Override
    public MutableComponent getInfoComponent() {
        return Component.translatable(getInfoTranslationKey(), IAttributeExtension.FORMAT.format(damageReduction * 100));
    }
}
