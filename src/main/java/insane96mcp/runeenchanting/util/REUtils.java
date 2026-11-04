package insane96mcp.runeenchanting.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;

public class REUtils {
    public static float getAttackStrengthScale(Entity entity) {
        if (entity instanceof Player player) {
            float f = player.getAttackStrengthScale(0.5f);
            return f * f;
        }
        return 1f;
    }

    /**
     * Like {@link #getAttackStrengthScale(Entity)}, but projectiles (arrows and thrown tridents) always count as fully charged, as the attack cooldown is irrelevant for them.
     */
    public static float getAttackStrengthScale(DamageSource damageSource) {
        if (damageSource.getDirectEntity() != damageSource.getEntity())
            return 1f;
        return getAttackStrengthScale(damageSource.getEntity());
    }

    /**
     * True if the damage was dealt by an arrow shot from a bow or crossbow. Thrown tridents are excluded, as they deal their damage based off the item's attack damage
     */
    public static boolean isArrowHit(DamageSource damageSource) {
        return damageSource.getDirectEntity() instanceof AbstractArrow
                && !(damageSource.getDirectEntity() instanceof ThrownTrident);
    }

    /**
     * True if the item has attack damage modifiers. Bows and crossbows don't, so they don't count as melee weapons.
     */
    public static boolean isMeleeWeapon(ItemStack stack) {
        return stack.getAttributeModifiers().modifiers().stream()
                .anyMatch(entry -> entry.attribute().equals(Attributes.ATTACK_DAMAGE));
    }

    /**
     * True if the damage was dealt directly by the entity (not by a projectile) while using an item that isn't a melee weapon, e.g. hitting something with a bow.
     */
    public static boolean isMeleeHitWithNonWeapon(ItemStack stack, DamageSource damageSource) {
        return damageSource.getDirectEntity() == damageSource.getEntity() && !isMeleeWeapon(stack);
    }

    public static boolean isAttackCharged(DamageSource damageSource) {
        Entity entity = damageSource.getEntity();
        if (!(entity instanceof Player player))
            return true;
        if (damageSource.getDirectEntity() != entity)
            return true; // Hit by a projectile/thrown weapon (e.g. a thrown trident), not a melee swing: the attack cooldown is irrelevant.
        return player.getAttackStrengthScale(0.5f) >= 0.9f;
    }
}
