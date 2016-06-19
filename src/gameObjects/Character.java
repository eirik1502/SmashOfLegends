package gameObjects;

import graphics.GraphicsObject;
import graphics.Renderable;
import javafx.scene.input.KeyCode;
import userInput.InputState;

public class Character implements Renderable {


	private double x;
	private double y;
	private double r;

	public Character( double startX, double startY ) {
		setX(startX);
		setY(startY);
		r = 32;
	}

	public void update(InputState inputState) {
		if (inputState.isKeyboardPressed(KeyCode.A)) {
			addX(-2);
		}
		if (inputState.isKeyboardPressed(KeyCode.D)) {
			addX(2);
		}
		if (inputState.isKeyboardPressed(KeyCode.W)) {
			addY(-2);
		}
		if (inputState.isKeyboardPressed(KeyCode.S)) {
			addY(2);
		}



	}

	@Override
	public void render(GraphicsObject graphics) {
		graphics.setFill("FF0000");
		graphics.fillCircle( x, y, r );

	}


	public void setX( double x) {
		this.x = x;
	}
	public void setY( double y) {
		this.y = y;
	}
	public void addX( double x) {
		this.x += x;
	}
	public void addY( double y){
		this.y += y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

}
