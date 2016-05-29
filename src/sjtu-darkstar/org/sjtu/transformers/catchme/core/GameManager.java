package org.sjtu.transformers.catchme.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sjtu.transformers.catchme.player.PlayerObject;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

public class GameManager implements ManagedObject, Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, ManagedReference<Game>> games;

	public GameManager() {
		games = new HashMap<String, ManagedReference<Game>>();
	}

	/**
	 * 创建频道
	 * @param name
	 * @return
	 */
	public Game createGame(String name) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		
		Game game = getGame(name);
		if (game != null)
			return game;
		
		game = new Game();
		
		ChannelManager channelManager = AppContext.getChannelManager();
		channelManager.createChannel(name, new GameChannelListener(), Delivery.RELIABLE);
		
		game.setName(name);
		games.put(name, manager.createReference(game));
		
		return game;
	}

	/**
	 * 单机
	 * @param name
	 * @param owner
	 * @return
	 */
	public Game createGame(String name, PlayerObject owner) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		
		Game game = getGame(name);
		if (game != null)
			return game;
		
		game = new Game(owner);
		
		game.setName(name);
		games.put(name, manager.createReference(game));
		return game;
	}

	public Game getGame(String name) {
		ManagedReference<Game> game = games.get(name);
		if (game == null)
			return null;
		return game.get();
	}

	public Boolean containsKey(String key) {
		return games.containsKey(key);
	}

	public void removeGame(String name) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);

		ManagedReference<Game> game = games.remove(name);
		manager.removeObject(game.get());
	}
}
