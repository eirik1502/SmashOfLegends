package gameObjects;

import rooms.Entity;
import game.GameRoom;


public class Equinox extends Entity {

	
	public static float p1StartX = 298f + 300f;
	public static float p1StartY = 435f;
	public static float p2StartX = 4489f - 300f;
	public static float p2StartY = 452f;

	
	
	public Equinox(float x, float y) {
		super(x, y, 0);
	}
	
	
	public void destroy() {
		GameRoom groom = (GameRoom)super.room;
		groom.removeEntity(this);
		
		
	}
}
