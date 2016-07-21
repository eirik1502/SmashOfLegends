package gameObjects;

import game.Game;
import game.GameRoom;
import graphics.Sprite;
import maths.TrigUtils;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import rooms.Entity;
import rooms.Room;
import rooms.Updateable;
import userInput.InputState;

public class Bullet extends Entity implements Updateable, Collideable{

	
	private float radius;
	private float speed = 4;
	
	private int timer = 60;
	
	private static Sprite sprite;
	
	public static void loadSprite() {
		sprite = new Sprite("res/bullet_cyber.png", 40, 8); //40, 8);
	}

	public Bullet(float startX, float startY, float direction, float speed) {
		super(sprite, startX, startY, direction);
		this.speed = speed;

	}


	@Override
	public void update() {

		GameRoom groom = (GameRoom)room;
		
		if (timer-- == 0) room.removeEntity(this);
			
		x += Math.cos(rotation)*speed;
		y += Math.sin(rotation)*speed;
		
		Enemy enemy = groom.collideEnemy(this);
		if (enemy != null) {
			enemy.addHp(-34);
			groom.removeEntity(this);
		}

//		Enemy enemy = game.getEnemy();
//		if (PhysicsHandeler.isCollision(this, enemy)) {
//			float knockback = 64;
//			//float direction = TrigUtils.pointDirection(x, y, enemy.getX(), enemy.getY());
//			enemy.setX(enemy.getX() + (float)Math.cos(direction)*knockback);
//			enemy.setY(enemy.getY() + (float)Math.sin(direction)*knockback);
//			game.removeUnit(this);
//		}
	}

	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}


}
