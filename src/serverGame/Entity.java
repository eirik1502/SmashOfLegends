package serverGame;

import rooms.Updateable;

public abstract class Entity implements Updateable {

	
	protected float x, y;
	
	protected float rotation;
	
	//protected Sprite sprite;
	
	protected ServerGame game;
	
	/**
	 * Subclasses should not use their constructor
	 * @param sprite
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public Entity(float x, float y, float rotation) {
		//this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public void gameInit(ServerGame game) {
		this.game = game;
		start();
	}
	
	protected abstract void start();


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
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	
	
	
}