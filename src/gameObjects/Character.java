package gameObjects;

import graphics.Font;
import graphics.Sprite;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import rooms.Entity;
import rooms.Room;
import rooms.Text;
import rooms.Updateable;
import userInput.InputState;
import static maths.TrigUtils.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFW.*;

import game.Game;
import game.GameRoom;


public class Character extends Entity implements Updateable, Collideable {


	private float radius;


	private int shootTimer = 0;
	private int shootDelay = 40;
	
	private Text text;
	

	public Character( float startX, float startY ) {
		super(new Sprite("res/frank_3_shotgun_strip13.png", 13, 40, 32, 0), 200, 100, 0);
		setX(startX);
		setY(startY);
		radius = 32;
		
		text = new Text("", Font.getStandardFont(), 18, getX(), getY()-16, 0f);
		
		super.getSprite().setImageSpeed(0);
		getSprite().setOnAnimationEnd(sprite->{sprite.setImageSpeed(0);});
	}
	
	@Override
	public void start() {
	}
	
	

	@Override
	public void update() {
		
		super.update();
		
		GameRoom groom = (GameRoom)room;
		
		
		float lastX = x;
		float lastY = y;
		
		float moveSpeed = 3;
		if (InputState.isKeyboardPressed(GLFW.GLFW_KEY_A)) {
			addX(-moveSpeed);

		}
		if (InputState.isKeyboardPressed(GLFW.GLFW_KEY_D)) {
			addX(moveSpeed);

		}
		if (InputState.isKeyboardPressed(GLFW.GLFW_KEY_W)) {
			addY(-moveSpeed);

		}
		if (InputState.isKeyboardPressed(GLFW.GLFW_KEY_S)) {
			addY(moveSpeed);

		}
		
		Collideable baundaryBox = new Collideable() {
			private PhShape boundaryBox = new PhRectangle(x-radius, y-radius, radius*2, radius*2);
			public PhShape getPhShape() {
				return boundaryBox;
			}
		};
		
		if (groom.collideBoard(baundaryBox)) {
			x = lastX;
			y = lastY;
		}
		
		text.setString("reloading, " + Integer.toString(shootTimer));
		text.setX(x-64);
		text.setY(y-64);


		double deltaY = InputState.getMouseY() - getY();
		double deltaX = InputState.getMouseX() - getX();
		double currRotation;
		if (deltaX == 0) currRotation = deltaY > 0? Math.PI/2 : 1.5*Math.PI;
		else {
			currRotation = Math.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += Math.PI;
		}
		super.rotation = (float)currRotation;

		if (shootTimer == 0) {
			if (InputState.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
				float bulletSpeed = 15+2*(float)Math.random();
				float offset = 96;
				Bullet bullet = new Bullet(getX()+lengthdirX(offset, rotation), getY()+lengthdirY(offset, rotation), rotation, bulletSpeed);
				sprite.setImageSpeed(1);
				room.addEntity( bullet);

				shootTimer = shootDelay;
			}
		}
		else {
			shootTimer--;
		}
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
