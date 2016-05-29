package org.sjtu.transformers.catchme.player.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerCommandHandler {
	private static final Logger logger = Logger.getLogger(PlayerCommandHandler.class.getName());
	private static Map<String, PlayerCommand> playerCmds = new HashMap<String, PlayerCommand>();
	static {
		playerCmds.put("CreateGame", new CreateGame());
		playerCmds.put("JoinGame", new JoinGame());
		playerCmds.put("Move", new Move());
		playerCmds.put("Login", new Login());
		playerCmds.put("SendMessage", new SendMessage());
		playerCmds.put("RemoveGame", new RemoveGame());
		playerCmds.put("LeaveGame", new LeaveGame());
		playerCmds.put("UseItem", new UseItem());
	}

	public static void handleCmd(PlayerCommandContext context, String cmd) {
		if (cmd == null) {
			logger.log(Level.WARNING, "cmd is null");
			return;
		}
		String[] cmdWithArgs = cmd.split(" ");
		if (cmdWithArgs.length < 1)
			return;
		
		String cmdName = cmdWithArgs[0];
		if (cmdName == null)
			return;
		
		PlayerCommand playerCmd = playerCmds.get(cmdName);
		// logger.log(Level.WARNING, cmdName + " " + Arrays.asList(cmdWithArgs) + " " + context);
		if(playerCmd != null) {
			playerCmd.execute(context, cmdWithArgs);
		} else {
			System.out.println("Òì³£Ö¸Áî : " + cmdName);
		}
	}

}
