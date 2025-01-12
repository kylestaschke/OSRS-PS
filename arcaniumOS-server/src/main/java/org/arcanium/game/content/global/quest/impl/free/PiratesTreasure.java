package org.arcanium.game.content.global.quest.impl.free;

import org.arcanium.game.component.Component;
import org.arcanium.game.content.global.quest.Quest;
import org.arcanium.game.content.global.quest.QuestState;
import org.arcanium.game.node.entity.player.Player;
import org.arcanium.game.node.item.GroundItemManager;
import org.arcanium.game.node.item.Item;

/**
 * Represents the pirates treasure quest.
 * @author 'Vexia
 * @date 28/12/2013
 */
public final class PiratesTreasure extends Quest {

    /**
     * Represents the message component for pirates treasure quest.
     */
    public static final Component MESSAGE_COMPONENT = new Component(222);

    /**
     * Represents the cakset rewards (pirates treasure).
     */
    public static final Item[] CASKET_REWARDS = new Item[] { new Item(995, 450), new Item(1635), new Item(1605) };

    /**
     * Represents the karamjan rum item.
     */
    public static final Item KARAMJAN_RUM = new Item(431);

    /**
     * Represents the chest key item.
     */
    public static final Item KEY = new Item(432);

    /**
     * Represents the pirate message item.
     */
    public static final Item PIRATE_MESSAGE = new Item(433);

    /**
     * Represents the casket item.
     */
    public static final Item CASKET = new Item(7956);

    /**
     * Constructs a new {@code PiratesTreasure} {@code Object}.
     * @param player
     */
    public PiratesTreasure(Player player) {
	super(player);
    }

    @Override
    public void update() {
	super.clear();
	switch (stage) {
	case 0:
	    player.getPacketDispatch().sendString(getBlue() + "I can start this quest by speaking to " + getRed() + "Redbeard Frank " + getBlue() + "who", 275, 4);
	    player.getPacketDispatch().sendString(getBlue() + "is at " + getRed() + " Port Sarim.", 275, 5);
	    player.getPacketDispatch().sendString(getBlue() + "There aren't any requirements for this quest.", 275, 7);
	    break;
	case 10:
	    if (!player.getInventory().containsItem(KARAMJAN_RUM)) {
		line(BLUE + "I have spoken to " + RED + " Redbeard Frank." + BLUE + " He has agreed to tell me", 4);
		line(BLUE + "the location of some " + RED + " treasure " + BLUE + "for some " + RED + "Karamja Rum.", 5);
		line(BLUE + "I have taken employment on the " + RED + "banana plantation " + BLUE + ", as the", 6);
		line(RED + "Customs Officers " + BLUE + "might not notice the " + RED + "rum " + BLUE + "if it is covered", 7);
		line(BLUE + "in " + RED + "bananas.", 8);
		line(BLUE + "Now all I need is some " + RED + " rum " + BLUE + "to hide in the next crate", 10);
		line(BLUE + "destined for " + RED + "Wydin's store" + BLUE + "...", 11);
	    } else {
		line(BLUE + "I have spoken to " + RED + "Redbeard Frank " + BLUE + ". He has agreed to tell me", 5);
		line(BLUE + "the location of some " + RED + "treasure " + BLUE + "for some " + RED + "Karamja Rum.", 6);
		line(BLUE + "I have the " + RED + "Karamja Rum. " + BLUE + "I should take it to " + RED + "Redbeard Frank.", 8);
	    }
	    break;
	case 20:
	    line("<str>I have smuggled some rum off Karamja, and retrieved it", 4);
	    line("<str>from the back room of Wydin's shop.", 5);
	    line(BLUE + "I have given the rum to " + RED + "Redbeard Frank." + BLUE + " He has told me", 7);
	    line(BLUE + "that the " + RED + "treasure " + BLUE + "is hidden in the chest in the upstairs", 8);
	    line(BLUE + "room of the " + RED + "Blue Moon Inn " + BLUE + "in " + RED + "Varrock.", 9);
	    if (player.getAttributes().containsKey("pirate-read")) {
		line(BLUE + "The note reads " + RED + "'Visit the city of the White Knights. In the", 11);
		line(RED + "park, Saradomin points to the X which marks the spot.'", 12);
		if (!player.getInventory().containsItem(PIRATE_MESSAGE) && !player.getBank().containsItem(PIRATE_MESSAGE)) {
		    line(BLUE + "It's a good job I remembered that, as I have lost the " + RED + "note.", 14);
		}
		return;
	    }
	    if (player.getInventory().containsItem(PIRATE_MESSAGE)) {
		line(BLUE + "I have opened the chest in the " + RED + "Blue Moon" + BLUE + ", and found a", 11);
		line(RED + "note " + BLUE + "inside. I think it will tell me where to dig.", 12);
		return;
	    }
	    if (player.getInventory().containsItem(KEY)) {
		line(BLUE + "I have a " + RED + "key " + BLUE + "that can be used to unlock the chest that", 11);
		line(BLUE + "holds the treasure.", 12);
	    } else {
		line(BLUE + "I have lost the " + RED + "key " + BLUE + "that " + RED + "Readbeard Frank " + BLUE + "gave me. I should", 11);
		line(BLUE + "see if he has another.", 12);
	    }
	    break;
	case 100:
	    line("<str>The note reads 'Visit the city of the White Knights. In the", 4);
	    line("<str>park, Saradomin points to the X which marks the spot.'", 5);
	    line("<col=FF0000>QUEST COMPLETE!", 7);
	    line(BLUE + "I've found a treasure, gained 2 Quest Points and gained", 9);
	    line(BLUE + "access to the Pay-fare option to travel to and from", 10);
	    line(BLUE + "Karamja!", 11);
	    break;
	}
    }

    @Override
    public void finish() {
	super.finalize();
	player.getPacketDispatch().sendString("2 Quest Points", 277, 8);
	player.getPacketDispatch().sendString("One-Eyes Hector's Treasure", 277, 9);
	player.getPacketDispatch().sendString("Chest", 277, 10);
	player.getPacketDispatch().sendString("You can also use the Pay-", 277, 11);
	player.getPacketDispatch().sendString("fare option to go to and from", 277, 12);
	player.getPacketDispatch().sendString("Karamja", 277, 13);
	player.getPacketDispatch().sendString("You have completed the Pirate's Treasure Quest!", 277, 2);
	player.getPacketDispatch().sendItemZoomOnInterface(7956, 230, 277, 3);
	player.removeAttribute("gardener-attack");
	player.removeAttribute("pirate-read");
	if (!player.getInventory().add(CASKET)) {
	    GroundItemManager.create(CASKET, player);
	}
    }

    @Override
    public int[] getConfig() {
	int id = 71;
	if (state == QuestState.COMPLETED) {
	    return new int[] { id, 4 };
	} else if (state == QuestState.STARTED) {
	    return new int[] { id, 1 };
	} else {
	    return new int[] { id, 0 };
	}
    }

    @Override
    public int getIndex() {
	return 23;
    }

    @Override
    public String getName() {
	return "Pirate's Treasure";
    }

    @Override
    public int getQuestPoints() {
	return 2;
    }
}
