package gameObjects;

import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import rooms.Entity;

public class Wall extends Entity implements Collideable{

	
	float width, height;
	private PhRectangle rect;
	
	public Wall(float x, float y, float width, float height) {
		super(x, y, 0);
		this.width = width;
		this.height = height;
		this.rect = new PhRectangle(x, y, width, height);
		
	}
	
	@Override
	public PhShape getPhShape() {
		return rect;
	}
}
