package org.sjtu.transformers.catchme.item;

import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.TaskManager;

public class Treasure extends Item {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(Treasure.class
			.getName());

	public Treasure() {

	}

	@Override
	public void affect(PlayerObject playerObject) {

		playerObject.setSpeed(Constants.SPEED_FAST);

		// logger.log(Level.INFO, "Player {0} gained speed! Speed={1}", new Object[] { playerObject, playerObject.getSpeed() });
		SendMsgUtil.sendToPlayer(playerObject, "Speed Double");
		TaskManager taskManager = AppContext.getTaskManager();
		DataManager dataManager = AppContext.getDataManager();
		taskManager.scheduleTask(new ItemEffectEliminator(dataManager.createReference(this), dataManager.createReference(playerObject)), Constants.TREASURE_SPEEDUP_PERIOD);
	}

	@Override
	public void deaffect(PlayerObject playerObject) {
		playerObject.setSpeed(Constants.SPEED_NORMAL);
		// logger.log(Level.INFO,"Player {0} restored speed to normal! Speed={1}", new Object[] {playerObject, playerObject.getSpeed() });
		SendMsgUtil.sendToPlayer(playerObject, "Speed Normal");

	}

	@Override
	public int getEffectPeriod() {
		return Constants.TREASURE_SPEEDUP_PERIOD;
	}

}
