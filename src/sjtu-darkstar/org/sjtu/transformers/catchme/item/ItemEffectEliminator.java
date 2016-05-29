package org.sjtu.transformers.catchme.item;

import java.io.Serializable;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.player.PlayerObject;

import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class ItemEffectEliminator implements Task, Serializable {

	private static final long serialVersionUID = 1L;
	private ManagedReference<? extends Item> item;
	private ManagedReference<PlayerObject> playerObject;

	private static final Logger logger = Logger.getLogger(ItemEffectEliminator.class.getName());

	public ItemEffectEliminator(ManagedReference<? extends Item> item, ManagedReference<PlayerObject> playerObject) {
		this.item = item;
		this.playerObject = playerObject;
	}

	/**
	 * 用任务的方式使item被playObject影响
	 */
	@Override
	public void run() throws Exception {
		item.get().deaffect(playerObject.get());
		// logger.log(Level.INFO, "Eliminate effect of {0} on {1}", new Object[]
		// {
		// playerObject, item });
	}

}
