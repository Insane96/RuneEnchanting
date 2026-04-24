package insane96mcp.runeenchanting.data;

import net.minecraft.world.entity.LivingEntity;

public class TickContext {
    public LivingEntity ticker;

    public TickContext(LivingEntity ticker) {
        this.ticker = ticker;
    }
}
