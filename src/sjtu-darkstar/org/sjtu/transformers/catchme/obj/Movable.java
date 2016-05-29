package org.sjtu.transformers.catchme.obj;

import org.sjtu.transformers.catchme.map.MapArea;

public interface Movable {
	public void moveTo(MapArea mapArea);
	public void moveTo(int y, int x);
	public double getSpeed();
	public double getDirection();
}
