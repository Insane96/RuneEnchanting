package insane96mcp.runeenchanting.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class MiningContext {
    public LivingEntity miner;
    ///For some reason, the BreakSpeed event has this Optional
    public Optional<BlockPos> pos;
    public Level level;
    public float originalSpeed;
    public float newSpeed = 0.0f;

    public MiningContext(LivingEntity miner, Optional<BlockPos> pos, Level level, float originalSpeed, float newSpeed) {
        this.miner = miner;
        this.pos = pos;
        this.level = level;
        this.originalSpeed = originalSpeed;
        this.newSpeed = newSpeed;
    }
}
