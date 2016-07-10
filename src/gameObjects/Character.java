package gameObjects;

import application.Game;
import application.Updateable;
import graphics.RenderObject;
import graphics.Renderable;
import graphics.Sprite;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import userInput.InputState;
import static maths.TrigUtils.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFW.*;


public class Character implements Renderable, Updateable, Collideable {


	private float x;
	private float y;
	private float radius;
	private float rotation;

	private Sprite sprite;

	private Game game;

	private int shootTimer = 0;
	private int shootDelay = 60/7;

	public Character( Game game, float startX, float startY ) {
		this.game = game;
		setX(startX);
		setY(startY);
		radius = 32;
		sprite = new Sprite("res/frank_original_swg.png", 32, 32);
	}

	@Override
	public void update(InputState inputState) {
		
		//System.out.println("Char pos: " + getX() + ", " + getY());
		
		if (inputState.isKeyboardPressed(GLFW.GLFW_KEY_A)) {
			addX(-2);
		}
		if (inputState.isKeyboardPressed(GLFW.GLFW_KEY_D)) {
			addX(2);
		}
		if (inputState.isKeyboardPressed(GLFW.GLFW_KEY_W)) {
			addY(-2);
		}
		if (inputState.isKeyboardPressed(GLFW.GLFW_KEY_S)) {
			addY(2);
		}


		double deltaY = inputState.getMouseY() - getY();
		double deltaX = inputState.getMouseX() - getX();
		double currRotation;
		if (deltaX == 0) currRotation = deltaY > 0? Math.PI/2 : 1.5*Math.PI;
		else {
			currRotation = Math.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += Math.PI;
		}
		rotation = (float)currRotation;

		if (shootTimer == 0) {
			if (inputState.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
				System.out.println("shoot");
				float offset = 96;
				Bullet bullet = new Bullet(game, getX()+lengthdirX(offset, rotation), getY()+lengthdirY(offset, rotation), rotation, 15+2*(float)Math.random());
				game.addUnit( bullet);

				shootTimer = shootDelay;
			}
		}
		else {
			shootTimer--;
		}
	}

	@Override
	public void render(RenderObject graphics) {
		//graphics.setFill("FF0000");
		graphics.drawSprite(sprite, getX(), getY(), rotation);
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
