package org.sjtu.transformers.catchme.player.cmd;

import java.util.logging.Logger;

import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;

public class SendMessage implements PlayerCommand {
	private static final Logger logger = Logger.getLogger(SendMessage.class
			.getName());

	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length < 2) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("SendMessage ");
		sb.append(context.getClientListener().getName());
		sb.append(" ");
		for (int i = 1; i < args.length; i++) {
			sb.append(args[i]);
			if (i != args.length - 1) {
				sb.append(" ");
			}
		}
		String message = sb.toString();

		String gameName = context.getGame().getName();

		Channel channel = AppContext.getChannelManager().getChannel(gameName);
		ClientSession clientSession = context.getClientSession();
		channel.send(clientSession, MessageEncoder.encodeString(message));

		// logger.log(Level.INFO,
		// "{0} ClientSession boardcast message via {1} channel.",
		// new Object[] { clientSession.getName(), gameName });
		clientSession.send(MessageEncoder.encodeString("SendMessage Done."));
	}
}
