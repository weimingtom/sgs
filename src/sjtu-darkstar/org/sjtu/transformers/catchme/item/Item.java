package org.sjtu.transformers.catchme.item;

import org.sjtu.transformers.catchme.obj.GameObject;
import org.sjtu.transformers.catchme.obj.GameObjectType;
import org.sjtu.transformers.catchme.player.PlayerObject;

public abstract class Item extends GameObject {
	private static final long serialVersionUID = 1L;
	private static int count = 1;
	private int id;
		
	public Item() {
		this.id = count++;
		if (count == 10000)
			count = 1;
	}

	public int getId() {
		return id;
	}	
	
	@Override
	public GameObjectType getType() {
		return GameObjectType.ITEM;
	}

	public abstract int getEffectPeriod();
	public abstract void affect(PlayerObject playerObject);
	public abstract void deaffect(PlayerObject playerObject);
}
