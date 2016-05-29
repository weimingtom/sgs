package org.sjtu.transformers.catchme.obj;

import org.sjtu.transformers.catchme.map.GameMap;
import org.sjtu.transformers.catchme.map.MapArea;

public interface Mappable {
	public MapArea getMapArea();
	public GameMap getGameMap();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object arg0);

}
