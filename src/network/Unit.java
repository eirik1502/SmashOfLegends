package network;


import graphics.Sprite;
import rooms.Entity;

public class Unit extends Entity {

	private Sprite sprite;
	
	public Unit(Sprite sprite, float x, float y) {
		super(sprite, 0, 0, 0);
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}
	
	

	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
		
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	
}