package gameObjects;

import graphics.Sprite;
import rooms.Entity;

public class Wall extends Entity {

	
	float width, height;
	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/wall.png", 0, 0); //40, 8);
	}
	
	public Wall(float x, float y, float width, float height) {
		super(sprite, x, y, 0);
		this.width = width;
		this.height = height;
	}

	
}
