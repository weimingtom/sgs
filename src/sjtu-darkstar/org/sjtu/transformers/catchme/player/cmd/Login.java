package org.sjtu.transformers.catchme.player.cmd;

import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.ClientSession;

public class Login implements PlayerCommand {

	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length != 2)
			return;
		String name = args[1];
		if (name == null)
			return;
		context.getClientListener().setName(name);
		ClientSession session = context.getClientSession();
		session.send(MessageEncoder.encodeString("Login Successfully."));
	}

}
