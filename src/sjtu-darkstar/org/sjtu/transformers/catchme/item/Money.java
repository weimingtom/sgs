package org.sjtu.transformers.catchme.item;

import java.util.logging.Logger;

import org.sjtu.transformers.catchme.player.PlayerObject;
import org.sjtu.transformers.catchme.player.PlayerRole;
import org.sjtu.transformers.catchme.util.SendMsgUtil;

public class Money extends Item {
	private static final long serialVersionUID = 1L;
	private int quantity;

	private static final Logger logger = Logger.getLogger(Money.class.getName());

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Money(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * moneyӰ��playerObject
	 */
	@Override
	public void affect(PlayerObject playerObject) {
		if (playerObject.getPlayerRole() == PlayerRole.THIEF) {
			
			int total = playerObject.getTotalCash();
			if (total + this.quantity > 99999) {
				playerObject.addTotalCash(99999 - total);
			} else {
				playerObject.addTotalCash(this.quantity);
			}
			
			SendMsgUtil.sendToPlayer(playerObject, "GetMoney " + (playerObject.getTotalCash() - total));

			// logger.log(Level.INFO, "Player {0} get money: {1}", new Object[]
			// {
			// playerObject, this.quantity });
		}
	}

	/**
	 * money��playerObjectӰ��
	 */
	@Override
	public void deaffect(PlayerObject playerObject) {

	}

	/**
	 * Ӱ��ļ��ʱ��
	 */
	@Override
	public int getEffectPeriod() {
		return 0;
	}
}
