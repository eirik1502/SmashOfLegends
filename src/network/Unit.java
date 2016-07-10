package network;

import graphics.RenderObject;
import graphics.Renderable;
import graphics.Sprite;

public class Unit implements Renderable{

	private Sprite sprite;
	private float x;
	private float y;
	
	public Unit(Sprite sprite, float x, float y) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void render(RenderObject graphics) {
		graphics.drawSprite(sprite, x,  y, 0);
		
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