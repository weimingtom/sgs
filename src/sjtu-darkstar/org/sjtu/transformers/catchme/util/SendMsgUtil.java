package org.sjtu.transformers.catchme.util;

import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.player.PlayerObject;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;

public class SendMsgUtil {
	public static void sendToAll(Game game, String msg) {
		String gameName = game.getName();

		Channel channel = AppContext.getChannelManager().getChannel(gameName);
		channel.send(null, MessageEncoder.encodeString(msg));
	}

	public static void sendToPlayer(PlayerObject playerObject, String msg) {
		ClientSession session = playerObject.getClientListener().getSession();
		session.send(MessageEncoder.encodeString(msg));
	}
}
