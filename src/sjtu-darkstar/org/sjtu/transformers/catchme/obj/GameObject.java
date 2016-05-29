package org.sjtu.transformers.catchme.obj;

import java.io.Serializable;
import java.math.BigInteger;

import org.sjtu.transformers.catchme.map.GameMap;
import org.sjtu.transformers.catchme.map.MapArea;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

public abstract class GameObject implements Mappable, ManagedObject, Serializable {
	private static final long serialVersionUID = 1L;

	private ManagedReference<GameMap> curGameMapRef = null;
	private ManagedReference<MapArea> curMapAreaRef;

	public abstract GameObjectType getType();

	protected BigInteger getUID() {
		DataManager manager = AppContext.getDataManager();
		return manager.getObjectId(this);
	}

	@Override
	public MapArea getMapArea() {
		if (curMapAreaRef == null)
			return null;
		return curMapAreaRef.get();
	}

	@Override
	public GameMap getGameMap() {
		if (curGameMapRef == null)
			return null;
		return curGameMapRef.get();
	}

	public void setGameMap(GameMap gameMap) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);
		if (gameMap == null) {
			curGameMapRef = null;
			return;
		}
		curGameMapRef = dataManager.createReference(gameMap);
	}

	public void setMapArea(MapArea mapArea) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);
		if (mapArea == null) {
			curMapAreaRef = null;
			return;
		}
		curMapAreaRef = dataManager.createReference(mapArea);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GameObject))
			return false;
		GameObject o = (GameObject) obj;
		return getUID().equals(o.getUID());
	}

	@Override
	public int hashCode() {
		return getUID().hashCode();
	}

	@Override
	public String toString() {
		return getType() + " # " + getUID();
	}
}
