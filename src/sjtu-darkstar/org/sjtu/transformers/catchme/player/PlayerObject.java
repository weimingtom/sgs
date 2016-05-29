package org.sjtu.transformers.catchme.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.ClientListenerImpl;
import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.core.GameManager;
import org.sjtu.transformers.catchme.item.Item;
import org.sjtu.transformers.catchme.item.Treasure;
import org.sjtu.transformers.catchme.map.MapArea;
import org.sjtu.transformers.catchme.obj.GameObject;
import org.sjtu.transformers.catchme.obj.GameObjectType;
import org.sjtu.transformers.catchme.obj.MovableGameObject;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

public class PlayerObject extends MovableGameObject {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PlayerObject.class
			.getName());
	protected static final String PLAYER_BIND_PREFIX = "Player.";
	private ManagedReference<ClientListenerImpl> clientListenerRef = null;
	private Set<ManagedReference<Item>> items;
	private int currentCash = 0;
	private int totalCash = 0;

	public void addCurrentCash(int cash) {
		currentCash += cash;
	}

	public void addTotalCash(int cash) {
		totalCash += cash;
		if (totalCash > Constants.PLAYER_MAX_MONEY) {
			totalCash = Constants.PLAYER_MAX_MONEY;
		} else if (totalCash < 0) {
			totalCash = 0;
		}
	}

	public int getCurrentCash() {
		return currentCash;
	}

	public int getTotalCash() {
		return totalCash;
	}

	private PlayerRole playerRole;

	public PlayerObject(ClientListenerImpl player, PlayerRole playerRole) {
		setClientListener(player);
		this.playerRole = playerRole;
		this.items = new HashSet<ManagedReference<Item>>();
	}

	private void setClientListener(ClientListenerImpl player) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		this.clientListenerRef = manager.createReference(player);
	}

	public ClientListenerImpl getClientListener() {
		if (clientListenerRef == null) {
			return null;
		}
		return clientListenerRef.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveTo(MapArea mapArea) {
		// logger.log(Level.INFO, "{0} moves to {1}", new Object[] { this, mapArea });

		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);

		getMapArea().removePlayer();
		setMapArea(mapArea);
		getMapArea().addPlayer(this);
	}

	public boolean addItem(Item item) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		return items.add(dataManager.createReference(item));
	}

	@Override
	public void moveTo(int y, int x) {
		// logger.log(Level.INFO, "{0} moves to {1},{2}", new Object[] { this, y, x});
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);

		MapArea area = getGameMap().getMapArea(y, x);
		moveTo(area);
	}

	public List<Item> getItemsList() {
		if (items.isEmpty()) {
			return Collections.emptyList();
		}
		ArrayList<Item> items = new ArrayList<Item>(this.items.size());
		for (ManagedReference<Item> itemRef : this.items) {
			Item item = itemRef.get();
			items.add(item);
		}
		return Collections.unmodifiableList(items);
	}

	public void useItem(Item item) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		item.affect(this);
		items.remove(dataManager.createReference(item));

		// logger.log(Level.INFO, "Item " + item.getClass().getSimpleName() + " used on player " + this);
	}

	@Override
	public GameObjectType getType() {
		return GameObjectType.PLAYER;
	}

	public void disconnected(boolean graceful) {
		getMapArea().removePlayer();
		DataManager dataManager = AppContext.getDataManager();
		GameManager manager = (GameManager) dataManager.getBinding(Constants.GAMEMANAGER_BINDING_NAME);

		Game game = manager.getGame(Constants.UNIVERSAL_GAME_NAME);
		game.removePlayer(this);

		setMapArea(null);
		SendMsgUtil.sendToAll(game, "LeaveGame " + this.getClientListener().getName());
	}

	public void setPlayerRole(PlayerRole playerRole) {
		this.playerRole = playerRole;
	}

	public PlayerRole getPlayerRole() {
		return playerRole;
	}

	@Override
	public void meet(GameObject object) {
		if (object instanceof PlayerObject) {
			PlayerObject playerObject = (PlayerObject) object;
			if (this.getPlayerRole() == playerObject.getPlayerRole()) {
				return;
			} else {
				DataManager dataManager = AppContext.getDataManager();
				GameManager manager = (GameManager) dataManager.getBinding(Constants.GAMEMANAGER_BINDING_NAME);
				Game game = manager.getGame(Constants.UNIVERSAL_GAME_NAME);

				if (this.getPlayerRole() == PlayerRole.POLICE) {
					SendMsgUtil.sendToAll(game, "Catch "
							+ this.getClientListener().getName() + " "
							+ playerObject.getClientListener().getName());

					int money = playerObject.getTotalCash();
					if (money > Constants.MINIMUM_MONEY_CATCH) {
						money = Constants.MINIMUM_MONEY_CATCH
								+ (int) ((money - Constants.MINIMUM_MONEY_CATCH) * Math.random());
						if (money > Constants.MAXIMUM_MONEY_CATCH)
							money = Constants.MAXIMUM_MONEY_CATCH;
					}
					int total = this.getTotalCash();
					this.addTotalCash(money);
					SendMsgUtil.sendToPlayer(this, "GetMoney " + (this.getTotalCash() - total));

					playerObject.addTotalCash(total - this.getTotalCash());
					SendMsgUtil.sendToPlayer(playerObject, "GetMoney " + (total - this.getTotalCash()));
					(new Treasure()).affect(playerObject);
				} else if (this.getPlayerRole() == PlayerRole.THIEF) {
					SendMsgUtil.sendToAll(game, "Catch "
							+ playerObject.getClientListener().getName() + " "
							+ this.getClientListener().getName());

					int money = this.getTotalCash();
					if (money > Constants.MINIMUM_MONEY_CATCH) {
						money = Constants.MINIMUM_MONEY_CATCH
								+ (int) ((money - Constants.MINIMUM_MONEY_CATCH) * Math
										.random());
						if (money > Constants.MAXIMUM_MONEY_CATCH)
							money = Constants.MAXIMUM_MONEY_CATCH;
					}
					int total = playerObject.getTotalCash();
					playerObject.addTotalCash(money);
					SendMsgUtil.sendToPlayer(playerObject, "GetMoney "
							+ (playerObject.getTotalCash() - total));

					this.addTotalCash(total - playerObject.getTotalCash());
					SendMsgUtil.sendToPlayer(this, "GetMoney "
							+ (total - playerObject.getTotalCash()));

					(new Treasure()).affect(this);
				}
			}
		} else if (object instanceof Item) {
			DataManager dataManager = AppContext.getDataManager();
			dataManager.markForUpdate(this);
			((Item) object).affect(this);
			// logger.log(Level.INFO, object.getClass().getSimpleName() + " got!");
		}
	}

	@Override
	public String toString() {
		String name = getClientListener().getName();
		if (name == null)
			return super.toString();
		return name;
	}
}

