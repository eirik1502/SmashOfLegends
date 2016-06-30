package maths;

public class TrigUtils {

	public static float pointDirection(float x1, float y1, float x2, float y2) {
		double deltaY = y2 - y1;
		double deltaX = x2 - x1;
		double currRotation;
		if (deltaX == 0) currRotation = deltaY > 0? Math.PI/2 : 1.5*Math.PI;
		else {
			currRotation = Math.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += Math.PI;
		}
		return (float)currRotation;
	}
	
	public static float lengthdirX(float length, float dir) {
		return (float)Math.cos(dir)*length;
	}
	public static float lengthdirY(float length, float dir) {
		return (float)Math.sin(dir)*length;
	}
}
