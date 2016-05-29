package org.sjtu.transformers.catchme.map;

import java.io.Serializable;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.item.Item;
import org.sjtu.transformers.catchme.player.PlayerObject;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

public class MapArea implements ManagedObject, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(MapArea.class
			.getName());

	private int y, x;

	private ManagedReference<GameMap> gameMap;
	private ManagedReference<PlayerObject> player = null;
	private ManagedReference<Item> item = null;
	private int height = 0;

	public MapArea() {
	}

	public MapArea(GameMap map, int y, int x, int height) {
		this.y = y;
		this.x = x;
		this.height = height;
		this.gameMap = AppContext.getDataManager().createReference(map);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public GameMap getGameMap() {
		return gameMap.get();
	}

	public boolean addPlayer(PlayerObject player) {
		// logger.log(Level.INFO, "{0} enters {1}", new Object[] { player, this
		// });
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		if (this.player == null) {
			this.player = dataManager.createReference(player);
			return true;
		} else
			return false;
	}

	public boolean removePlayer() {
		// logger.log(Level.INFO, "{0} leaves {1}", new Object[] { player, this
		// });

		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		if (player == null) {
			return false;
		} else {
			player = null;
			return true;
		}
		// return players.remove(dataManager.createReference(player));
	}

	public boolean addItem(Item item) {
		// logger.log(Level.INFO, "{0} put to {1}", new Object[] { item, this
		// });

		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		if (this.item == null) {
			this.item = dataManager.createReference(item);
			return true;
		} else
			return false;

	}

	public boolean removeItem() {
		// logger.log(Level.INFO, "{0} removed from {1}", new Object[] { item,
		// this
		// });

		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		if (this.item == null) {
			return false;
		} else {
			this.item = null;
			return true;
		}
	}

	public PlayerObject getPlayer() {
		if (player == null) {
			return null;
		}
		return player.get();
	}

	public Item getItem() {
		if (item == null) {
			return null;
		}
		return item.get();
	}

	@Override
	public String toString() {
		return "Map Area X=" + x + ", Y=" + y + " in Map: " + getGameMap();
	}
}
