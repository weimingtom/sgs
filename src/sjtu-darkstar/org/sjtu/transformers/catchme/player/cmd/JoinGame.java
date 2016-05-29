package org.sjtu.transformers.catchme.player.cmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.ClientListenerImpl;
import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.core.GameManager;
import org.sjtu.transformers.catchme.map.GameMap;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.player.PlayerRole;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;

public class JoinGame implements PlayerCommand {
	private static final Logger logger = Logger.getLogger(JoinGame.class.getName());

	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length != 3) {
			return;
		}

		String gameName = args[1];
		if (gameName == null) {
			return;
		}
		gameName = gameName.trim();

		PlayerRole role = null;

		if (args[2] == null) {
			return;
		}
		if (args[2].equalsIgnoreCase("police")) {
			role = PlayerRole.POLICE;
		} else if (args[2].equalsIgnoreCase("thief")) {
			role = PlayerRole.THIEF;
		} /*
		 * else if (args[2].equalsIgnoreCase("dummy")) { role =
		 * PlayerRole.DUMMY; } else if (args[2].equalsIgnoreCase("ghost")) {
		 * role = PlayerRole.GHOST; }
		 */

		DataManager dataManager = AppContext.getDataManager();
		GameManager gameManager = (GameManager) dataManager
				.getBinding(Constants.GAMEMANAGER_BINDING_NAME);
		Game game = gameManager.getGame(gameName);

		if (game == null)
			return;

		ClientListenerImpl clientListener = context.getClientListener();
		PlayerObject playerObject = clientListener.joinGame(game, role);

		// Send all players info to new player
		game.sendPlayersInfoToPlayer(playerObject);

		StringBuffer sb = new StringBuffer();
		sb.append("JoinGame ");
		sb
				.append(playerObject.getMapArea().getY()
						* Constants.MAP_CLIENT_PIXELS
						+ Constants.MAP_CLIENT_PIXELS / 2);
		sb.append(" ");
		sb
				.append(playerObject.getMapArea().getX()
						* Constants.MAP_CLIENT_PIXELS
						+ Constants.MAP_CLIENT_PIXELS / 2);

		ClientSession clientSession = context.getClientSession();

		ChannelManager channelManager = AppContext.getChannelManager();
		channelManager.getChannel(gameName).join(clientSession);

		logger.log(Level.INFO, "{0} ClientSession has added to {1} channel.",
				new Object[] { clientSession.getName(), gameName });

		String msg = "JoinGame " + playerObject + " "
				+ playerObject.getPlayerRole().toString().toLowerCase();
		SendMsgUtil.sendToAll(game, msg);

		GameMap map = game.getGameMap();
		map.sendItemsInfoToPlayer(playerObject);

		SendMsgUtil.sendToPlayer(playerObject, sb.toString());
		SendMsgUtil.sendToPlayer(playerObject, "JoinGame Done.");

	}
}
