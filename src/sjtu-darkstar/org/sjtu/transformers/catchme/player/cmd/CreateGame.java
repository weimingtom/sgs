package org.sjtu.transformers.catchme.player.cmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.GameManager;
import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;

public class CreateGame implements PlayerCommand {
	private static final Logger logger = Logger.getLogger(CreateGame.class.getName());

	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length != 2)
			return;
		
		String name = args[1];
		if (name == null)
			return;
		name = name.trim();

		DataManager dataManager = AppContext.getDataManager();
		GameManager manager = (GameManager) dataManager.getBinding(Constants.GAMEMANAGER_BINDING_NAME);
		manager.createGame(name);
		ClientSession clientSession = context.getClientSession();
		logger.log(Level.INFO, "{0} channel has created by {1} ClientSession.", new Object[] { name, clientSession.getName() });
		clientSession.send(MessageEncoder.encodeString("CreateGame Done."));
	}
}
