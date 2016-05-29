package org.sjtu.transformers.catchme.core;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.player.PlayerRole;
import org.sjtu.transformers.catchme.player.cmd.PlayerCommandContext;
import org.sjtu.transformers.catchme.player.cmd.PlayerCommandHandler;
import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;

public class ClientListenerImpl implements ClientSessionListener, ManagedObject, Serializable {
	private static final long serialVersionUID = 1L;
	private static final String logName = "Player";
	private static final Logger logger = Logger.getLogger(logName);
	protected static final String PLAYER_BIND_PREFIX = "Player.";


	private ManagedReference<ClientSession> currentSessionRef;
	private ManagedReference<Game> currentGameRef;
	private ManagedReference<PlayerObject> currentPlayerObjRef;

	private String name = "Guest";

	/**
	 * 查找玩家是否在数据仓库中绑定（需要ClientSession的name），不存在就创建然后绑定
	 * @param session
	 * @return
	 */
	public static ClientListenerImpl loggedIn(ClientSession session) {
		String playerBinding = PLAYER_BIND_PREFIX + session.getName();
		DataManager dataMgr = AppContext.getDataManager();
		ClientListenerImpl player;
		
		try {
			player = (ClientListenerImpl) dataMgr.getBinding(playerBinding);
		} catch (NameNotBoundException ex) {
			player = new ClientListenerImpl();
			logger.log(Level.INFO, "New player created: {0}", player);
			dataMgr.setBinding(playerBinding, player);
		}
		player.setSession(session);
		return player;
	}

	public void setPlayerObject(PlayerObject playerObj) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		if (playerObj == null) {
			return;
		}
		this.currentPlayerObjRef = manager.createReference(playerObj);
	}

	public PlayerObject getPlayerObject() {
		if (currentPlayerObjRef == null) {
			return null;
		}
		return currentPlayerObjRef.get();
	}

	/**
	 * 让role加入game
	 * @param game
	 * @param role
	 * @return
	 */
	public PlayerObject joinGame(Game game, PlayerRole role) {
		DataManager manager = AppContext.getDataManager();
		manager.markForUpdate(this);
		
		if (game == null) {
			return null;
		}
		this.currentGameRef = manager.createReference(game);
		PlayerObject playerObj = new PlayerObject(this, role);
		setPlayerObject(playerObj);
		game.addPlayer(playerObj);
		
		int x = playerObj.getMapArea().getX();
		int y = playerObj.getMapArea().getY();
		logger.log(Level.INFO, "{0} enters {1} as {2} at (X={3},Y={4})", new Object[] { this, game, role, x, y });
		
		return playerObj;
	}

	public Game getGame() {
		if (currentGameRef == null) {
			return null;
		}
		return currentGameRef.get();
	}

	public ClientSession getSession() {
		if (currentSessionRef == null) {
			return null;
		}
		return currentSessionRef.get();
	}

	/**
	 * TODO:允许session为空
	 * @param session
	 */
	protected void setSession(ClientSession session) {
		DataManager dataMgr = AppContext.getDataManager();
		dataMgr.markForUpdate(this);
		currentSessionRef = (session == null) ? null : dataMgr.createReference(session);
		logger.log(Level.INFO, "Set session for {0} to {1}", new Object[] {this, session});
	}

	@Override
	public void disconnected(boolean graceful) {
		if (getSession() != null) {
			ChannelManager channelManager = AppContext.getChannelManager();
			channelManager.getChannel(Constants.UNIVERSAL_GAME_NAME).leave(getSession());
		}

		logger.log(Level.INFO, "Disconnected: {0} graceful: ", new Object[] {this, graceful});
		
		/**
		 * 如果出现事务会滚，会造成空指针错误
		 */
		PlayerObject playerObject = getPlayerObject();
		if(playerObject != null)
			playerObject.disconnected(graceful);
	}

	/**
	 * 协议驱动
	 */
	public void receivedMessage(ByteBuffer message) {
		String cmd = MessageEncoder.decodeString(message);
		// logger.log(Level.INFO, "{0} received command: {1}", new Object[] {
		// this, cmd });
		System.out.println("接收命令" + cmd);
		PlayerCommandContext context = buildPlayerCmdContext();
		PlayerCommandHandler.handleCmd(context, cmd);
	}

	protected PlayerCommandContext buildPlayerCmdContext() {
		ClientSession session = getSession();
		PlayerObject playerObj = getPlayerObject();
		Game game = getGame();
		PlayerCommandContext context = new PlayerCommandContext(this, session, game, playerObj);
		return context;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
