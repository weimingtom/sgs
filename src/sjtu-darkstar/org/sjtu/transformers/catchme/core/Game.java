package org.sjtu.transformers.catchme.core;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.map.GameMap;
import org.sjtu.transformers.catchme.map.ItemGenerator;
import org.sjtu.transformers.catchme.map.MapArea;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.TaskManager;

/**
 * 维护地图和玩家表的数据结构
 * @author Administrator
 *
 */
public class Game implements ManagedObject, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Game.class.getName());

	private ManagedReference<GameMap> gameMap;	
	private ManagedReference<PlayerObject> owner;
	private Set<ManagedReference<PlayerObject>> players;
	private ItemGenerator itemGenerator;

	private Random random = new Random(47);
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemGenerator getItemGenerator() {
		return itemGenerator;
	}

	public Game() {
		DataManager dataManager = AppContext.getDataManager();
		TaskManager taskManager = AppContext.getTaskManager();
		
		this.players = new HashSet<ManagedReference<PlayerObject>>();
		this.gameMap = dataManager.createReference(new GameMap(Constants.MAP_SIZE_Y, Constants.MAP_SIZE_X));
		this.itemGenerator = new ItemGenerator(dataManager.createReference(this));

		taskManager.schedulePeriodicTask(new ItemGenerator(dataManager.createReference(this)), 0, Constants.MAP_ITEM_REGENERATION_INTERVAL);
	}

	public Game(PlayerObject owner) {
		this();
		setOwner(owner);
	}
	
	/**
	 * 把所有玩家的信息告诉给某客户端playerObject
	 * @param playerObject
	 */
	public void sendPlayersInfoToPlayer(PlayerObject playerObject) {
		for (ManagedReference<PlayerObject> playerRef : players) {
			if (playerRef.get() != null && 
				!playerObject.getClientListener().getName().equals(playerRef.get().getClientListener().getName())) {
				String msg = "JoinGame "
						+ playerRef.get().toString()
						+ " "
						+ playerRef.get().getPlayerRole().toString().toLowerCase();
				SendMsgUtil.sendToPlayer(playerObject, msg);

				StringBuffer sb = new StringBuffer();
				sb.append("Move ");
				sb.append(playerRef.get().toString());
				sb.append(" ");
				sb.append(playerRef.get().getMapArea().getY() * Constants.MAP_CLIENT_PIXELS + Constants.MAP_CLIENT_PIXELS / 2);
				sb.append(" ");
				sb.append(playerRef.get().getMapArea().getX() * Constants.MAP_CLIENT_PIXELS + Constants.MAP_CLIENT_PIXELS / 2);

				SendMsgUtil.sendToPlayer(playerObject, sb.toString());
			}
		}
	}



	public void setGameMap(GameMap gameMap) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		if (gameMap == null)
			return;
		this.gameMap = dataManager.createReference(gameMap);
	}

	public GameMap getGameMap() {
		if (gameMap == null)
			return null;
		return gameMap.get();
	}

	public void setOwner(PlayerObject owner) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);
		
		if(owner == null)
			return;
		this.owner = dataManager.createReference(owner);
	}

	public PlayerObject getOwner() {
		if (owner == null)
			return null;
		return owner.get();
	}

	public boolean addPlayer(PlayerObject playerObj) {
		logger.log(Level.INFO, "{0} enters {1}", new Object[] { playerObj, this });
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		randomPositon(playerObj);
		playerObj.setGameMap(this.getGameMap());
		
		return players.add(dataManager.createReference(playerObj));
	}
	
	public boolean removePlayer(PlayerObject playerObj) {
		logger.log(Level.INFO, "{0} leaves {1}", new Object[] { playerObj, this });
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);

		return players.remove(dataManager.createReference(playerObj));
	}
	
	/**
	 * 设置playerObj随机出现位置
	 * @param playerObj
	 */
	private void randomPositon(PlayerObject playerObj) {
		int y = random.nextInt(Constants.MAP_SIZE_Y);
		int x = random.nextInt(Constants.MAP_SIZE_X);
		GameMap gameMap = getGameMap();
		MapArea area = gameMap.getMapArea(y, x);
		area.addPlayer(playerObj);
		playerObj.setGameMap(gameMap);
		playerObj.setMapArea(area);
	}



	public List<PlayerObject> getPlayersList() {
		if (players.isEmpty()) {
			return Collections.emptyList();
		}
		ArrayList<PlayerObject> players = new ArrayList<PlayerObject>(this.players.size());
		for (ManagedReference<PlayerObject> playerRef : this.players) {
			PlayerObject player = playerRef.get();
			players.add(player);
		}
		return Collections.unmodifiableList(players);
	}

	@Override
	public String toString() {
		BigInteger uid = AppContext.getDataManager().getObjectId(this);
		return "Game # " + uid;
	}

}
