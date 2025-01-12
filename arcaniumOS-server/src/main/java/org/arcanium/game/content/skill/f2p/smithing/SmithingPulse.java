package org.arcanium.game.content.skill.f2p.smithing;

import org.arcanium.cache.def.impl.ItemDefinition;
import org.arcanium.game.content.global.tutorial.TutorialSession;
import org.arcanium.game.content.global.tutorial.TutorialStage;
import org.arcanium.game.content.skill.SkillPulse;
import org.arcanium.game.content.skill.Skills;
import org.arcanium.game.node.entity.player.Player;
import org.arcanium.game.node.entity.player.info.portal.Perks;
import org.arcanium.game.node.item.Item;
import org.arcanium.game.world.update.flag.context.Animation;
import org.arcanium.tools.StringUtils;

/**
 * Represents the pulse used to smith a bar.
 * @author 'Vexia
 */
public class SmithingPulse extends SkillPulse<Item> {

    /**
     * Represents the animation to use.
     */
    private static final Animation ANIMATION = new Animation(898);

    /**
     * Represents the bar being made.
     */
    private final Bars bar;

    /**
     * Represents the amount to make.
     */
    private int amount;

    /**
     * Constructs a new {@code SmithingPulse} {@code Object}.
     * @param player the player.
     * @param item the item.
     */
    public SmithingPulse(Player player, Item item, Bars bar, int ammount) {
	super(player, item);
	this.bar = bar;
	this.amount = ammount;
    }

    @Override
    public boolean checkRequirements() {
	if (!player.getInventory().contains(bar.getBarType().getBarType(), bar.getSmithingType().getRequired() * amount)) {
	    amount = player.getInventory().getAmount(new Item(bar.getBarType().getBarType()));
	}
	player.getInterfaceManager().close();
	if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevel()) {
	    player.getDialogueInterpreter().sendDialogue("You need a Smithing level of " + bar.getLevel() + " to make a " + ItemDefinition.forId(bar.getProduct()).getName() + ".");
	    return false;
	}
	if (!player.getInventory().contains(bar.getBarType().getBarType(), bar.getSmithingType().getRequired())) {
	    player.getDialogueInterpreter().sendDialogue("You don't have enough " + ItemDefinition.forId(bar.getBarType().getBarType()).getName().toLowerCase() + "s to make a " + bar.getSmithingType().name().replace("TYPE_", "").replace("_", " ").toLowerCase() + ".");
	    return false;
	}
	if (!player.getInventory().contains(2347, 1)) {
	    player.getDialogueInterpreter().sendDialogue("You need a hammer to work the metal with.");
	    return false;
	}
	if (TutorialSession.getExtension(player).getStage() <= TutorialSession.MAX_STAGE && node.getId() != Bars.BRONZE_DAGGER.getProduct()) {
	    return false;
	}
	return true;
    }

    @Override
    public void animate() {
	player.animate(ANIMATION);
    }

    @Override
    public boolean reward() {
	if (getDelay() == 1) {
	    setDelay(4);
	    return false;
	}
	player.getInventory().remove(new Item(bar.getBarType().getBarType(), bar.getSmithingType().getRequired()));
	Perks.addDouble(player, (new Item(node.getId(), bar.getSmithingType().getProductAmount())));
	player.getSkills().addExperience(Skills.SMITHING, bar.getBarType().getExperience() * bar.getSmithingType().getRequired(), true);
	String message = StringUtils.isPlusN(ItemDefinition.forId(bar.getProduct()).getName().toLowerCase()) == true ? "an" : "a";
	player.getPacketDispatch().sendMessage("You hammer the " + bar.getBarType().getBarName().toLowerCase().replace("smithing", "") + "and make " + message + " " + ItemDefinition.forId(bar.getProduct()).getName().toLowerCase() + ".");
	if (TutorialSession.getExtension(player).getStage() == 42) {
	    TutorialStage.load(player, 43, false);
	}
	amount--;
	return amount < 1;
    }

    @Override
    public void message(int type) {

    }

}
