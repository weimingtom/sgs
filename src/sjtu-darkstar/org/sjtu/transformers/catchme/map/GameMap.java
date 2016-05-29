package org.sjtu.transformers.catchme.map;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.item.Item;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

public class GameMap implements ManagedObject, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GameMap.class.getName());

	private int sizeY, sizeX;
	private int[][] height;
	private boolean initialized = false;

	private ManagedReference<MapArea>[][] areas;
	private ConcurrentLinkedQueue<ManagedReference<MapArea>> items;

	@SuppressWarnings("unchecked")
	public GameMap(int sizeY, int sizeX) {
		this.sizeY = sizeY;
		this.sizeX = sizeX;
		this.height = new HeightGenerator(sizeY, sizeX).getHeight();
		this.areas = new ManagedReference[sizeY][sizeX];
		this.items = new ConcurrentLinkedQueue<ManagedReference<MapArea>>();
		initialize();
	}

	public void initialize() {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		MapArea area;
		for (int y = 0; y < sizeY; y++) {
			for (int x = 0; x < sizeX; x++) {
				area = new MapArea(this, y, x, height[y][x]);
				areas[y][x] = manager.createReference(area);
			}
		}

		// logger.log(Level.INFO, "Height set in gameMap {1} ",
		// new Object[] { this });

		initialized = true;
	}

	public void sendItemsInfoToPlayer(PlayerObject playerObject) {
		for (ManagedReference<MapArea> itemArea : items) {
			Item item = itemArea.get().getItem();
			StringBuffer sb = new StringBuffer();
			sb.append("Gen" + item.getClass().getSimpleName() + " ");
			sb.append(item.getId());
			sb.append(" ");
			sb.append(itemArea.get().getY() * Constants.MAP_CLIENT_PIXELS
					+ Constants.MAP_CLIENT_PIXELS / 2);
			sb.append(" ");
			sb.append(itemArea.get().getX() * Constants.MAP_CLIENT_PIXELS
					+ Constants.MAP_CLIENT_PIXELS / 2);
			SendMsgUtil.sendToPlayer(playerObject, sb.toString());
		}
	}

	/**
	 * 这个成员要在任务中使用所以一定要线程安全
	 * @return
	 */
	public ConcurrentLinkedQueue<ManagedReference<MapArea>> getItems() {
		return items;
	}

	public MapArea getMapArea(int y, int x) {
		ManagedReference<MapArea> area = this.areas[y][x];
		if (area == null) {
			return null;
		}
		return area.get();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeX() {
		return sizeX;
	}

}
