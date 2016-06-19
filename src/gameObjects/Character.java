package gameObjects;

import application.Game;
import application.Updateable;
import graphics.GraphicsObject;
import graphics.Renderable;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import userInput.InputState;

public class Character implements Renderable, Updateable {


	private double x;
	private double y;
	private double r;
	private double rotation;

	private Image image;


	private Game game;

	private int shootTimer = 0;
	private int shootDelay = 60;

	public Character( Game game, double startX, double startY ) {
		this.game = game;
		setX(startX);
		setY(startY);
		r = 32;
		image = new Image("guy.png");
	}

	@Override
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


		double deltaY = inputState.getMouseY() - getY();
		double deltaX = inputState.getMouseX() - getX();
		double currRotation;
		if (deltaX == 0) currRotation = deltaY < 0? Math.PI/2 : 1.5*Math.PI;
		else {
			currRotation = Math.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += Math.PI;
		}
		rotation = currRotation;

		if (shootTimer == 0) {
			if (inputState.isMousePressed(MouseButton.PRIMARY)) {
				System.out.println("shoot");
				Bullet bullet = new Bullet(getX(), getY(), rotation, 8);
				game.addUnit( bullet);

				shootTimer = shootDelay;
			}
		}
		else {
			shootTimer--;
		}
	}

	@Override
	public void render(GraphicsObject graphics) {
		//graphics.setFill("FF0000");

		graphics.drawImage(image, getX(), getY(), 32, 32, rotation );
		System.out.println(rotation);
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
