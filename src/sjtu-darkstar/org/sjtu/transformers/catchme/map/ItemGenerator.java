package org.sjtu.transformers.catchme.map;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.item.Item;
import org.sjtu.transformers.catchme.item.Money;
import org.sjtu.transformers.catchme.item.Treasure;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class ItemGenerator implements Task, Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ItemGenerator.class.getName());
	public ConcurrentLinkedQueue<ManagedReference<MapArea>> getItems() {
		return getGameItems();
	}

	private Random rand = new Random(System.currentTimeMillis());
	private ManagedReference<Game> gameRef;

	/**
	 * ‘ –Ì≈◊“Ï≥£
	 * @return
	 */
	private ConcurrentLinkedQueue<ManagedReference<MapArea>> getGameItems() {
		return gameRef.get().getGameMap().getItems();
	}

	public ItemGenerator(ManagedReference<Game> gameRef) {
		this.gameRef = gameRef;
	}

	@Override
	public void run() throws Exception {
		for (ManagedReference<MapArea> mapAreaRef : getGameItems()) {
			if (mapAreaRef.get().getItem() == null) {
				getGameItems().remove(mapAreaRef);
			}
		}

		if (getGameItems().size() < Constants.MAP_MAXIMUM_ITEMS) {
			while (getGameItems().size() < Constants.MAP_MAXIMUM_ITEMS) {
				getGameItems().add(AppContext.getDataManager().createReference(generateRandomItem()));
			}
		} else {
			ManagedReference<MapArea> firstArea = getGameItems().remove();
			String itemType = firstArea.get().getItem().getClass().getSimpleName();
			SendMsgUtil.sendToAll(gameRef.get(), "Remove" + itemType + " " + firstArea.get().getItem().getId());
			firstArea.get().removeItem();
			getGameItems().add(AppContext.getDataManager().createReference(generateRandomItem()));
		}
	}

	private MapArea generateRandomItem() {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(gameRef.get());
		int index = rand.nextInt(Constants.MAP_SIZE_X * Constants.MAP_SIZE_Y);
		MapArea targetArea = gameRef.get().getGameMap().getMapArea(index / Constants.MAP_SIZE_Y, index % Constants.MAP_SIZE_X);
		while (targetArea.getItem() != null) {
			index = rand.nextInt(Constants.MAP_SIZE_X * Constants.MAP_SIZE_Y);
			targetArea = gameRef.get().getGameMap().getMapArea(index / Constants.MAP_SIZE_Y, index % Constants.MAP_SIZE_X);
		}

		int money = Constants.MAP_ITEM_MINIMUM_MONEY + rand.nextInt(Constants.MAP_ITEM_MAXIMUM_MONEY - Constants.MAP_ITEM_MINIMUM_MONEY) / 10 * 10;

		Item[] newItems = new Item[] { new Money(money), new Treasure() };
		Item newItem = newItems[rand.nextInt(newItems.length)];
		targetArea.addItem(newItem);

		StringBuffer sb = new StringBuffer();
		sb.append("Gen" + newItem.getClass().getSimpleName() + " ");
		sb.append(targetArea.getItem().getId());
		sb.append(" ");
		sb.append(targetArea.getY() * Constants.MAP_CLIENT_PIXELS + Constants.MAP_CLIENT_PIXELS / 2);
		sb.append(" ");
		sb.append(targetArea.getX() * Constants.MAP_CLIENT_PIXELS + Constants.MAP_CLIENT_PIXELS / 2);

		SendMsgUtil.sendToAll(gameRef.get(), sb.toString());
		return targetArea;
	}
}
