package insane96mcp.runeenchanting.data;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class AttackContext {
    public LivingEntity attacker;
    public LivingEntity attacked;
    public float damage;
    public DamageSource damageSource;
    public boolean isCritical;

    public AttackContext(LivingEntity attacker, LivingEntity attacked, float damage, DamageSource damageSource, boolean isCritical) {
        this.attacker = attacker;
        this.attacked = attacked;
        this.damage = damage;
        this.damageSource = damageSource;
        this.isCritical = isCritical;
    }
}
