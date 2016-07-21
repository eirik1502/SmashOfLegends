package gameObjects;

import game.Game;
import game.GameRoom;
import graphics.Sprite;
import maths.TrigUtils;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import rooms.Entity;
import rooms.Updateable;
import trash.RenderObject;
import trash.Renderable;
import userInput.InputState;

public class Enemy extends Entity implements Updateable, Collideable {


	private int hp = 100;
	
	private float radius;
	
	private float direction = 0;
	private float directionOffset = 0;
	private float speed = 2;
	
	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/guy.png", 32, 32); //40, 8);
	}
	
	public Enemy( float x, float y) {
		super(sprite, x, y, 0f);
		this.radius = 32;

	}


	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}


	@Override
	public void update() {
		GameRoom groom = (GameRoom)room;
		Character player = groom.getPlayer();
		
		float directionToPlayer = TrigUtils.pointDirection(x, y, player.getX(), player.getY());
		directionOffset += 0.1 * (0.5 - Math.random());
		directionOffset = Math.max(directionOffset, -1);
		directionOffset = Math.min(directionOffset, 1);
		direction = directionToPlayer + directionOffset;
		rotation = direction;
		
		speed += (0.5-Math.random()) *0.1;
		speed = Math.max(speed, 1);
		speed = Math.min(speed, 3);
		
		x += Math.cos(direction)*speed;
		y += Math.sin(direction)*speed;
		
		checkDead();
		checkAttack();
	}
	private void checkDead() {
		GameRoom groom = (GameRoom)room;
		if (hp <= 0) {
			groom.removeEnemy(this);
		}
	}
	private void checkAttack() {
		GameRoom groom = (GameRoom)room;
		Character player = groom.collidePlayer(this);
		if (player != null) {
			groom.gameOver();
			
		}
	}
	

	public void addHp(int hp) {
		setHp(getHp()+hp);
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getHp() {
		return hp;
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
	
	
	public float getDirection() {
		return direction;
	}
	public void setDirection(float direction) {
		this.direction = direction;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	
}
