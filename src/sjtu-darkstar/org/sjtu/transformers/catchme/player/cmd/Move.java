package org.sjtu.transformers.catchme.player.cmd;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.item.Money;
import org.sjtu.transformers.catchme.item.Treasure;
import org.sjtu.transformers.catchme.map.MapArea;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.player.PlayerRole;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

public class Move implements PlayerCommand {
	@Override
	public void execute(PlayerCommandContext context, String[] args) {
		if (args == null || args.length != 3) {
			return;
		}
		int cy = Integer.parseInt(args[1]);
		int cx = Integer.parseInt(args[2]);
		int sy = cy / Constants.MAP_CLIENT_PIXELS;
		int sx = cx / Constants.MAP_CLIENT_PIXELS;
		PlayerObject playerObject = context.getPlayerObject();

		int curX = playerObject.getMapArea().getX(), curY = playerObject
				.getMapArea().getY();

		String username = context.getClientListener().getName();
		SendMsgUtil.sendToAll(context.getGame(), "Move " + username + " " + cy
				+ " " + cx);

		if (curX == sx && curY == sy)
			return;

		MapArea targetArea = context.getGame().getGameMap().getMapArea(sy, sx);

		if (targetArea.getItem() != null) {

			if (targetArea.getItem() instanceof Money
					&& context.getPlayerObject().getPlayerRole() == PlayerRole.THIEF) {

				playerObject.meet(targetArea.getItem());
				SendMsgUtil.sendToAll(context.getGame(), "RemoveMoney "
						+ targetArea.getItem().getId());

				targetArea.removeItem();
			} else if (targetArea.getItem() instanceof Treasure) {
				playerObject.meet(targetArea.getItem());
				SendMsgUtil.sendToAll(context.getGame(), "RemoveTreasure "
						+ targetArea.getItem().getId());
				targetArea.removeItem();
			}

		}
		if (targetArea.getPlayer() != null) {
			playerObject.meet(targetArea.getPlayer());
		}
		playerObject.moveTo(sy, sx);
	}
}

