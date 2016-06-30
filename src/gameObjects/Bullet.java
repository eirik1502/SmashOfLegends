package gameObjects;

import application.Updateable;
import graphics.RenderObject;
import graphics.Renderable;
import graphics.Sprite;
import userInput.InputState;

public class Bullet implements Renderable, Updateable{


	private float x;
	private float y;
	private float r;
	private float direction;
	private float speed;
	
	private static Sprite sprite;
	
	public static void loadSprite() {
		sprite = new Sprite("res/bullet.png", 8, 40); //40, 8);
	}

	public Bullet(float startX, float startY, float direction, float speed) {
		this.x = startX;
		this.y = startY;
		this.direction = direction;
		this.speed = speed;
		

		r = 4;
	}


	@Override
	public void render(RenderObject graphics) {
		graphics.drawSprite(sprite, x, y, direction);

	}


	@Override
	public void update(InputState inputState) {
		x += Math.cos(direction)*speed;
		y += Math.sin(direction)*speed;

	}


}
