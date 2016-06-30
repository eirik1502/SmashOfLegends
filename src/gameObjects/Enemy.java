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
import userInput.InputState;

public class Enemy implements Updateable, Renderable, Collideable {


	private Game game;
	
	private float x, y, radius;
	private Sprite sprite;
	
	private float viewDirection = 0;
	private float direction = 0;
	private float speed = 2;
	
	
	public Enemy(Game game, float x, float y) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.radius = 32;
		
		sprite = new Sprite("res/guy.png", 32, 32);
	}


	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}


	@Override
	public void render(RenderObject graphics) {
		graphics.drawSprite(sprite, x, y, viewDirection);
		
	}


	@Override
	public void update(InputState inputState) {
		
		Character c = game.getCharacter();
		direction += 0.5 - Math.random();
		viewDirection = TrigUtils.pointDirection(x, y, c.getX(), c.getY());
		
		x += Math.cos(direction)*speed;
		y += Math.sin(direction)*speed;
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
