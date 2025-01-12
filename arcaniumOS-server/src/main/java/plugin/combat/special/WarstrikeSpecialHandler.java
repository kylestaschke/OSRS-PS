package plugin.combat.special;

import org.arcanium.game.content.skill.Skills;
import org.arcanium.game.node.entity.Entity;
import org.arcanium.game.node.entity.combat.BattleState;
import org.arcanium.game.node.entity.combat.CombatStyle;
import org.arcanium.game.node.entity.combat.handlers.MeleeSwingHandler;
import org.arcanium.game.node.entity.impl.Animator;
import org.arcanium.game.node.entity.player.Player;
import org.arcanium.game.node.entity.player.link.audio.Audio;
import org.arcanium.game.world.update.flag.context.Animation;
import org.arcanium.game.world.update.flag.context.Graphics;
import org.arcanium.plugin.Plugin;
import org.arcanium.tools.RandomFunction;

/**
 * Handles the Warstrike special attack.
 *
 * @author Emperor
 */
public final class WarstrikeSpecialHandler extends MeleeSwingHandler implements Plugin<Object> {

    /**
     * The special energy required.
     */
    private static final int SPECIAL_ENERGY = 100;

    /**
     * The attack animation.
     */
    private static final Animation ANIMATION = new Animation(7073, Animator.Priority.HIGH);

    /**
     * The graphic.
     */
    private static final Graphics GRAPHIC = new Graphics(1223);

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.MELEE.getSwingHandler().register(11804, this);
        CombatStyle.MELEE.getSwingHandler().register(20370, this);
        return this;
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        int hit = 0;
        if (isAccurateImpact(entity, victim, CombatStyle.MELEE, 1.049, 0.98)) {
            hit = RandomFunction.random(calculateHit(entity, victim, 1.1));
        }
        state.setEstimatedHit(hit);
        if (victim instanceof Player) {
            ((Player) victim).getPacketDispatch().sendMessage("You have been drained.");
        }
        int left = -victim.getSkills().updateLevel(Skills.DEFENCE, -hit, 0);
        if (left > 0) {
            left = -victim.getSkills().updateLevel(Skills.STRENGTH, -left, 0);
            if (left > 0) {
                left = -victim.getSkills().updateLevel(Skills.ATTACK, -left, 0);
                if (left > 0) {
                    left = (int) -(victim.getSkills().getPrayerPoints() + left);
                    victim.getSkills().decrementPrayerPoints(left);
                    if (left > 0) {
                        left = -victim.getSkills().updateLevel(Skills.MAGIC, -left, 0);
                        if (left > 0)
                            victim.getSkills().updateLevel(Skills.RANGE, -left, 0);
                    }
                }
            }
        }
        entity.asPlayer().getAudioManager().send(new Audio(3834), true);
        return 1;
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        entity.visualize(ANIMATION, GRAPHIC);
    }
}