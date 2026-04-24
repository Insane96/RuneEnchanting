package insane96mcp.runeenchanting.data.runes;

import insane96mcp.runeenchanting.data.AttackContext;
import insane96mcp.runeenchanting.data.MiningContext;
import insane96mcp.runeenchanting.data.TickContext;
import insane96mcp.runeenchanting.setup.RERunes;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;

public abstract class Rune {
    private final int priority = 0;
    @Nullable
    private String descriptionId;

    public Rune() {

    }

    public Component getName() {
        return Component.translatable(this.getNameLangId());
    }

    public Component getDescription() {
        return Component.translatable(this.getDescriptionLangId());
    }

    protected String getOrCreateNameLangId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("rune", RERunes.REGISTRY.getKey(this));
        }

        return this.descriptionId;
    }

    public String getNameLangId() {
        return this.getOrCreateNameLangId();
    }

    public String getDescriptionLangId() {
        return this.getOrCreateNameLangId() + ".description";
    }

    public ResourceLocation getApplicableToItemTag() {
        ResourceLocation id = RERunes.REGISTRY.getKey(this);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "rune_appliable_to/" + id.getPath());
    }

    public float onMiningSpeed(MiningContext context) {
        return context.originalSpeed;
    }

    public float onAttack(AttackContext context) {
        return context.damage;
    }

    public void onLivingTick(TickContext context) {

    }

    public void addAttributeModifiers(ItemAttributeModifierEvent event) {

    }

    public int getPriority() {
        return this.priority;
    }

    //TODO Add a method to override fill the default applicable to item tag
}
