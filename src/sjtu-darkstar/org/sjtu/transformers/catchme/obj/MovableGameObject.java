package org.sjtu.transformers.catchme.obj;

import org.sjtu.transformers.catchme.core.Constants;

public abstract class MovableGameObject extends GameObject implements Movable {
	private static final long serialVersionUID = 1L;
	
	private double direction = 0.0;
	private double speed = Constants.SPEED_NORMAL;

	@Override
	public double getDirection() {
		return direction;
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public abstract void meet(GameObject object);
}
