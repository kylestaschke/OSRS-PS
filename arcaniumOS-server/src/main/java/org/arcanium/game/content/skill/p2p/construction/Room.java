package org.arcanium.game.content.skill.p2p.construction;

import org.arcanium.cache.def.impl.ObjectDefinition;
import org.arcanium.game.node.entity.player.Player;
import org.arcanium.game.node.object.GameObject;
import org.arcanium.game.node.object.ObjectBuilder;
import org.arcanium.game.world.map.Direction;
import org.arcanium.game.world.map.Region;
import org.arcanium.game.world.map.RegionChunk;
import org.arcanium.game.world.map.RegionManager;

/**
 * Represents a room.
 * @author Emperor
 */
public final class Room {

    /**
     * The room properties.
     */
    private final RoomProperties properties;

    /**
     * The region chunk.
     */
    private RegionChunk chunk;

    /**
     * The hotspots.
     */
    private Hotspot[] hotspots;

    /**
     * The current rotation of the room.
     */
    private Direction rotation = Direction.NORTH;

    /**
     * Constructs a new {@code Room} {@code Object}.
     * @param properties The room properties.
     */
    public Room(RoomProperties properties) {
	this.properties = properties;
    }

    /**
     * Creates a new room.
     * @param player The player.
     * @param properties The room properties.
     * @return The room.
     */
    public static Room create(Player player, RoomProperties properties) {
	Room room = new Room(properties);
	room.configure(player.getHouseManager().getStyle());
	return room;
    }

    /**
     * Configures the room.
     */
    public void configure(HousingStyle style) {
	this.hotspots = new Hotspot[properties.getHotspots().length];
	for (int i = 0; i < hotspots.length; i++) {
	    hotspots[i] = properties.getHotspots()[i].copy();
	}
	decorate(style);
    }

    /**
     * Redecorates the room.
     * @param style The house style.
     */
    public void decorate(HousingStyle style) {
	Region region = RegionManager.forId(style.getRegionId());
	Region.load(region, true);
	RegionChunk roomChunk = region.getPlanes()[style.getPlane()].getRegionChunk(getProperties().getChunkX(), getProperties().getChunkY());
	setChunk(roomChunk.copy(region.getPlanes()[0]));
	getChunk().setObjects(roomChunk.getObjects().clone());
    }
    
    /**
     * Loads the room decorations
     * @param buildMode
     */
    public void loadDecorations(boolean buildMode) {
	System.out.println("Loading Decorations... "  + buildMode);
	for (int i = 0; i < hotspots.length; i++) {
	    Hotspot spot = hotspots[i];
	    int x = spot.getChunkX();
	    int y = spot.getChunkY();
	    if (spot.getDecorationIndex() > -1) {
		GameObject[][] objects = chunk.getObjects();
		if (objects[x][y] != null) {
		    objects[x][y] = objects[x][y].transform(spot.getHotspot().getDecorations()[spot.getDecorationIndex()].getObjectId());
		}
	    }
	}
	
	/* Portal */
	if (rotation != Direction.NORTH && getChunk().getRotation() == 0) {
	    getChunk().setRotation(rotation.toInteger());
	}
	
	if (!buildMode) {	    
	    for (int x = 0; x < getChunk().getObjects().length; x++) {
		for (int y = 0; y < getChunk().getObjects()[x].length; y++) {
		    GameObject o = getChunk().getObjects()[x][y];
		    if (o != null) {
			System.out.println("Object Name: " + o.getDefinition().getName() + " Object ID: " + o.getId() + " X: " + o.getLocation().getX() + " Y: " + o.getLocation().getY() + " Object Type: " + o.getType() + " hasBuild?: " + ObjectDefinition.forId(o.getId()).hasAction("build"));
			if (ObjectDefinition.forId(o.getId()).hasAction("build")) {
			    //LandscapeParser.removeGameObject(o);
			    System.out.println("Removing...");
			    ObjectBuilder.remove(o);
			    //getChunk().getObjects()[x][y] = null;
			    
			    /*Location l = getChunk().getCurrentBase();
			    l.transform(x, y, 0);
			    GameObject oo = RegionManager.getObject(l);
			    if (oo != null) {
				//ObjectBuilder.remove(oo);
				System.out.println("Removing at x: " + x + " y: " + y);

			    } else {
				System.out.println("Probably null");
			    }*/
			    
			    
			    //LandscapeParser.removeGameObject(new GameObject(o.getId(), o.getLocation(), o.getType(), o.getRotation()));
			   // System.out.println("Removing Build Object");
			}
			if (o.getId() == 13830 || o.getId() == Decoration.CURTAINS.getObjectId()) {
			    //chunk.
			}
		    }
		}
	    }
	}
    }
    

    /**
     * Loads all the decorations.
     * @param chunk The chunk used in the dynamic region.
     * @param buildingMode If building mode is enabled. public void
     * loadDecorations(BuildRegionChunk chunk, boolean buildingMode) { for (int
     * i = 0; i < hotspots.length; i++) { Hotspot spot = hotspots[i]; int x =
     * spot.getChunkX(); int y = spot.getChunkY(); if (spot.getDecorationIndex()
     * > -1) { GameObject[][] objects = chunk.getObjects(chunk.getIndex(x, y,
     * spot.getHotspot().getObjectId())); if (objects[x][y] != null) {
     * objects[x][y] = objects[x][y].transform(spot.getHotspot().getDecorations
     * ()[spot.getDecorationIndex()].getObjectId()); } } } if (rotation !=
     * Direction.NORTH && chunk.getRotation() == 0) { chunk.rotate(rotation); }
     * if (!buildingMode) { for (GameObject[] objects : chunk.getObjects()) {
     * for (GameObject o : objects) { if (o != null) { if
     * (o.getDefinition().hasAction("build")) { chunk.remove(o); }
     * System.err.println("LOL" + o); if (o.getId() == 13830 || o.getId() ==
     * BuildHotspot.CURTAINS.getObjectId()) { chunk.add(o.transform(13099,
     * o.getRotation(), 0)); } } } } } }
     */

    /**
     * Gets the exit directions.
     * @return The directions at which you can exit the room (0=east, 1=south,
     * 2=west, 3=north).
     */
    public boolean[] getExits() {
	boolean[] exits = { isExit(7, 3), isExit(3, 0), isExit(0, 3), isExit(3, 7) };
	if (rotation != Direction.NORTH && chunk.getRotation() == 0) {
	    for (int i = 0; i < RoomBuilder.DIRECTIONS.length; i++) {
		if (rotation == RoomBuilder.DIRECTIONS[i]) {
		    break;
		}
		boolean b = exits[0];
		for (int j = 0; j < exits.length - 1; j++) {
		    exits[j] = exits[j + 1];
		}
		exits[exits.length - 1] = b;
	    }
	}
	return exits;
    }

    /**
     * Checks if the object on the given chunk coordinates is a door.
     * @param chunkX The x location in the chunk.
     * @param chunkY The y location in the chunk.
     * @return {@code True} if so.
     */
    boolean isExit(int chunkX, int chunkY) {
	GameObject object = chunk.getObjects()[chunkX][chunkY];
	if (object != null && object.getType() < 4 && object.getName() != null) {
	    return true;
	}
	return false;
    }

    /**
     * Gets the chunk.
     * @return The chunk.
     */
    public RegionChunk getChunk() {
	return chunk;
    }

    /**
     * Sets the chunk.
     * @param chunk The chunk to set.
     */
    public void setChunk(RegionChunk chunk) {
	this.chunk = chunk;
    }

    /**
     * Gets the hotspots.
     * @return The hotspots.
     */
    public Hotspot[] getHotspots() {
	return hotspots;
    }

    /**
     * Sets the hotspots.
     * @param hotspots The hotspots to set.
     */
    public void setHotspots(Hotspot[] hotspots) {
	this.hotspots = hotspots;
    }

    /**
     * Gets the properties.
     * @return The properties.
     */
    public RoomProperties getProperties() {
	return properties;
    }

    /**
     * Sets the room rotation.
     * @param rotation The rotation.
     */
    public void setRotation(Direction rotation) {
	this.rotation = rotation;
    }

    /**
     * Gets the rotation.
     * @return The rotation.
     */
    public Direction getRotation() {
	return rotation;
    }

}