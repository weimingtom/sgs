package org.sjtu.transformers.catchme.player.cmd;

import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.util.MessageEncoder;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;

public class LeaveGame implements PlayerCommand {

	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		PlayerObject playerObject = context.getPlayerObject();
		Game game = context.getGame();
		game.removePlayer(playerObject);
		SendMsgUtil.sendToAll(game, "LeaveGame "
				+ playerObject.getClientListener().getName());
		ClientSession clientSession = context.getClientSession();
		String gameName = game.getName();
		ChannelManager channelManager = AppContext.getChannelManager();
		channelManager.getChannel(gameName).leave(clientSession);
		clientSession.send(MessageEncoder.encodeString("LeaveGame Done."));
	}
}

