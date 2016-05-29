package org.sjtu.transformers.catchme.player.cmd;

import java.util.List;

import org.sjtu.transformers.catchme.item.Item;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.util.MessageEncoder;

import com.sun.sgs.app.ClientSession;

public class UseItem implements PlayerCommand {
	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		PlayerObject playerObject = context.getPlayerObject();
		List<Item> items = playerObject.getItemsList();
		String msg;
		if (items.isEmpty())
			msg = "No item to use.";
		else {
			playerObject.useItem(items.get(0));
			msg = "UseItem Done.";
		}
		ClientSession clientSession = context.getClientSession();
		clientSession.send(MessageEncoder.encodeString(msg));
	}
}
