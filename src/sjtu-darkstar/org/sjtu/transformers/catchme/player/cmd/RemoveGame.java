package org.sjtu.transformers.catchme.player.cmd;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.GameManager;
import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;

public class RemoveGame implements PlayerCommand {
	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length != 2)
			return;
		String gameName = args[1];
		if (gameName == null)
			return;
		gameName = gameName.trim();

		DataManager dataManager = AppContext.getDataManager();
		GameManager manager = (GameManager) dataManager
				.getBinding(Constants.GAMEMANAGER_BINDING_NAME);

		ClientSession clientSession = context.getClientSession();

		if (manager.containsKey(gameName)) {
			manager.removeGame(gameName);
		} else {
			clientSession.send(MessageEncoder.encodeString(gameName
					+ " doesn't exist."));
			return;
		}
		ChannelManager channelManager = AppContext.getChannelManager();
		channelManager.getChannel(gameName).leaveAll();
		clientSession.send(MessageEncoder.encodeString("RemoveGame Done."));
	}

}
