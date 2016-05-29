package org.sjtu.transformers.catchme.client;

import java.awt.Point;

/**
 * 角色类，用于指定运动策略，数据结构和持久性
 * @author Administrator
 *
 */
public class CharacterEx {
	public static final int RATE_FACT = ClientTest3.RATE_FACT;  //速度因子	
	
	private Point point = new Point();
	private Point targetPoint = new Point();
	private double x_inc;
	private double y_inc;
	
	
	public CharacterEx() {
		point.x = point.y = 0;
		targetPoint.x = targetPoint.y = 0;
		x_inc = y_inc = 0;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point _point) {
		point.x = _point.x;
		point.y = _point.y;
	}

	public Point getTargetPoint() {
		return targetPoint;
	}
	
	public void setTargetPoint(Point _targetPoint) {
		targetPoint.x = _targetPoint.x;
		targetPoint.y = _targetPoint.y;
		
		//计算速度时假设它是匀速的，所以要使x_inc^2 + y_inc^2是一个常数
		double distance = Math.hypot((targetPoint.x - point.x) , (targetPoint.y - point.y));
		x_inc = RATE_FACT * (targetPoint.x - point.x) / distance;
		y_inc = RATE_FACT * (targetPoint.y - point.y) / distance;
	}
	
	private boolean isMoveEnd = true;
	//返回发送消息
	public boolean move() {
		if(Math.abs(targetPoint.x - point.x) <= Math.abs(x_inc) ||
		   Math.abs(targetPoint.y - point.y) <= Math.abs(y_inc)) {
				isMoveEnd = true;
		}
		
		if(Math.abs(targetPoint.x - point.x) > Math.abs(x_inc)) {
			point.x += x_inc;
		}

		if(Math.abs(targetPoint.y - point.y) > Math.abs(y_inc))	{
			point.y += y_inc;
		}
		
		if(isMoveEnd) {
			setTargetPoint(new Point(
					(int)(ClientTest3.WINDOW_WIDTH * Math.random()), 
					(int)(ClientTest3.WINDOW_HEIGHT * Math.random())
			));
			isMoveEnd = false;
			return true;
		}
		
		return false;
	}
}
