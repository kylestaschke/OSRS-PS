package plugin.combat.special;

import org.arcanium.game.node.entity.Entity;
import org.arcanium.game.node.entity.combat.BattleState;
import org.arcanium.game.node.entity.combat.CombatStyle;
import org.arcanium.game.node.entity.combat.handlers.MeleeSwingHandler;
import org.arcanium.game.node.entity.impl.Animator;
import org.arcanium.game.node.entity.player.Player;
import org.arcanium.game.world.update.flag.context.Animation;
import org.arcanium.game.world.update.flag.context.Graphics;
import org.arcanium.plugin.Plugin;
import org.arcanium.tools.RandomFunction;

/**
 * Handles the ancient mace special attack "Favour of the War God".
 *
 * @author Emperor
 */
public final class AncientMaceSpecialHandler extends MeleeSwingHandler implements Plugin<Object> {

    /**
     * The special energy required.
     */
    private static final int SPECIAL_ENERGY = 100;

    /**
     * The attack animation.
     */
    private static final Animation ANIMATION = new Animation(6147, Animator.Priority.HIGH);

    /**
     * The graphic.
     */
    private static final Graphics GRAPHIC = new Graphics(1052);

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.MELEE.getSwingHandler().register(11061, this);
        return this;
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        int hit = 0;
        if (isAccurateImpact(entity, victim, CombatStyle.MELEE, 1.1, 0.98)) {
            hit = RandomFunction.random(calculateHit(entity, victim, 1));
            if (entity.getSkills().getPrayerPoints() < entity.getSkills().getStaticLevel(5)) {
                entity.getSkills().setPrayerPoints(entity.getSkills().getPrayerPoints() + hit);
            }
            victim.getSkills().decrementPrayerPoints(hit);
        }
        state.setEstimatedHit(hit);
        return 1;
    }

    @Override
    protected int getFormatedHit(Entity entity, Entity victim, BattleState state, int hit) {
        // Disables protection prayers.
        return formatHit(entity, victim, hit);
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        entity.visualize(ANIMATION, GRAPHIC);
    }
}