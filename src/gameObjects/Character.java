package gameObjects;

import graphics.Font;
import graphics.Sprite;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import rooms.Entity;
import rooms.RelevantInputState;
import rooms.Room;
import rooms.Text;
import rooms.Updateable;
import userInput.InputState;
import static maths.TrigUtils.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFW.*;

import game.Game;
import game.GameRoom;


public class Character extends Entity implements Collideable {


	private float radius;

	private int hp = 100;

	private int shootTimer = 0;
	private int shootDelay = 40;
	
	private Text text;
	
	private float startX, startY;
	public GameRoom gameRoom;
	

	public Character( float startX, float startY ) {
		super(200, 100, 0);
		//new Sprite("res/frank_3_shotgun_strip13.png", 13, 40, 32, 0)
		setX(startX);
		setY(startY);
		this.startX = startX;
		this.startY = startY;
		radius = 32;
		
		//text = new Text("", Font.getStandardFont(), 18, getX(), getY()-16, 0f);
		
		//super.getSprite().setImageSpeed(0);
		//getSprite().setOnAnimationEnd(sprite->{sprite.setImageSpeed(0);});
	}
	
	@Override
	public void start() {
		gameRoom = (GameRoom)super.room;
	}
	

	public void update(RelevantInputState is) {
		super.update();
		
		GameRoom groom = (GameRoom)room;
		
		float lastX = x;
		float lastY = y;
		
		float moveSpeed = 3;
		if (is.isMoveLeft()) {
			addX(-moveSpeed);

		}
		if (is.isMoveRight()) {
			addX(moveSpeed);

		}
		if (is.isMoveUp()) {
			addY(-moveSpeed);

		}
		if (is.isMoveDown()) {
			addY(moveSpeed);

		}
		
		if (gameRoom.collideHole(this)) {
			respawn();
		}
		if (gameRoom.collideWall(this)) {
			setX(lastX);
			setY(lastY);
		}
		
//		Collideable baundaryBox = new Collideable() {
//			private PhShape boundaryBox = new PhRectangle(x-radius, y-radius, radius*2, radius*2);
//			public PhShape getPhShape() {
//				return boundaryBox;
//			}
//		};
		
//		if (groom.collideBoard(baundaryBox)) {
//			x = lastX;
//			y = lastY;
//		}
		
//		text.setString("reloading, " + Integer.toString(shootTimer));
//		text.setX(x-64);
//		text.setY(y-64);


		double deltaY = is.getMouseY() - getY();
		double deltaX = is.getMouseX() - getX();
		double currRotation;
		if (deltaX == 0) currRotation = deltaY > 0? Math.PI/2 : 1.5*Math.PI;
		else {
			currRotation = Math.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += Math.PI;
		}
		super.rotation = (float)currRotation;

		if (shootTimer == 0) {
			if (is.isAction1()) {
				//float bulletSpeed = 15+2*(float)Math.random();
				float offset = 96;
				Bullet bullet = new BasicBullet(getX()+lengthdirX(offset, rotation), getY()+lengthdirY(offset, rotation), rotation);
				//sprite.setImageSpeed(1);
				groom.addBullet( bullet);

				shootTimer = shootDelay;
			}
			else if (is.isAction2()) {
				//float bulletSpeed = 15+2*(float)Math.random();
				float offset = 96;
				Bullet bullet = new SpecialBullet(getX()+lengthdirX(offset, rotation), getY()+lengthdirY(offset, rotation), rotation);
				//sprite.setImageSpeed(1);
				groom.addBullet( bullet);

				shootTimer = shootDelay;
			}
		}
		else {
			shootTimer--;
		}
		
		checkDead();
	}


	private void checkDead() {
		if (hp <= 0) {
			respawn();
		}
	}
	
	private void respawn(){
		hp = 100;
		setX(startX);
		setY(startY);
	}
	
	public void addHp(int hp) {
		this.hp += hp;
	}
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}


	public void setX( float x) {
		this.x = x;
	}
	public void setY( float y) {
		this.y = y;
	}
	public void addX( float x) {
		this.x += x;
	}
	public void addY( float y){
		this.y += y;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	
}
