package serverGame.entities;

import graphics.Sprite;
import maths.TrigUtils;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import rooms.Room;
import rooms.Updateable;
import serverGame.Entity;
import serverGame.Game;
import serverGame.ServerGame;
import userInput.InputState;

public abstract class Bullet extends Entity implements Updateable, Collideable{

	
	//must be overriden
	
	
	private byte typeNumber;
	private float radius;
	private float damage;
	private float knockback;
	
	public ServerGame gameRoom;
	
	private float speed;
	
	private int timer;
	


	public Bullet(byte typeNumber, float radius, float damage, float knockback, float startX, float startY, float direction, float speed, int timer) {
		super(startX, startY, direction);
		this.typeNumber = typeNumber;
		this.radius = radius;
		this.damage = damage;
		this.knockback = knockback;
		this.speed = speed;
		this.timer = timer;
	}
	
	@Override
	public void start() {
		gameRoom = (ServerGame)this.room;
	}
	
	public abstract void onPlayerCollision(Character c);

	public void destroy() {
		gameRoom.removeBullet(this);
	}
	public void applyDamage(Character c, int damage) {
		c.addHp(-damage);
	}

	@Override
	public void update() {
		
		if (timer-- == 0) room.removeEntity(this);
			
		x += Math.cos(rotation)*speed;
		y += Math.sin(rotation)*speed;
		
		Character character = gameRoom.collideCharacter(this);
		if (character != null) {
			onPlayerCollision(character);
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

	
	public byte getTypeNumber() {
		return typeNumber;
	}

	public float getRadius() {
		return radius;
	}

	public float getDamage() {
		return damage;
	}

	public float getKnockback() {
		return knockback;
	}

	public float getSpeed() {
		return speed;
	}
	
	public String toString() {
		return "[Bullet, x: "+getX()+" y: "+getY()+" dir: "+getRotation()+" speed: "+getSpeed()+"]";
	}

}
