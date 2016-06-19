package gameObjects;

import application.Updateable;
import graphics.GraphicsObject;
import graphics.Renderable;
import userInput.InputState;

public class Bullet implements Renderable, Updateable{


	private double x;
	private double y;
	private double r;
	private double direction;
	private double speed;


	public Bullet(double startX, double startY, double direction, double speed) {
		this.x = startX;
		this.y = startY;
		this.direction = direction;
		this.speed = speed;

		r = 4;
	}


	@Override
	public void render(GraphicsObject graphics) {
		graphics.setFill("0000ff");
		graphics.fillCircle(x, y, r );

	}


	@Override
	public void update(InputState inputState) {
		x += Math.cos(direction)*speed;
		y += Math.sin(direction)*speed;

	}


}
