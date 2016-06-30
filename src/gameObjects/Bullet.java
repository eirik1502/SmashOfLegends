package gameObjects;

import application.Game;
import application.Updateable;
import graphics.RenderObject;
import graphics.Renderable;
import graphics.Sprite;
import maths.TrigUtils;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import userInput.InputState;

public class Bullet implements Renderable, Updateable, Collideable{

	
	private Game game;

	private float x;
	private float y;
	private float radius;
	private float direction;
	private float speed;
	
	private int timer = 60;
	
	private static Sprite sprite;
	
	public static void loadSprite() {
		sprite = new Sprite("res/bullet_cyber.png", 40, 8); //40, 8);
	}

	public Bullet(Game game, float startX, float startY, float direction, float speed) {
		this.game = game;
		this.x = startX;
		this.y = startY;
		this.direction = direction;
		this.speed = speed;
		

		radius = 4;
	}


	@Override
	public void render(RenderObject graphics) {
		graphics.drawSprite(sprite, x, y, direction);

	}


	@Override
	public void update(InputState inputState) {
		if (timer-- == 0) game.removeUnit(this);
			
		x += Math.cos(direction)*speed;
		y += Math.sin(direction)*speed;

		Enemy enemy = game.getEnemy();
		if (PhysicsHandeler.isCollision(this, enemy)) {
			float knockback = 32;
			float direction = TrigUtils.pointDirection(x, y, enemy.getX(), enemy.getY());
			enemy.setX(enemy.getX() + (float)Math.cos(direction)*knockback);
			enemy.setY(enemy.getY() + (float)Math.sin(direction)*knockback);
			game.removeUnit(this);
		}
	}

	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}


}
