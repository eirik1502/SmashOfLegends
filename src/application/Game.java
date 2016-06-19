package application;

import gameObjects.Character;
import graphics.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import physics.*;
import userInput.InputHandeler;
import userInput.InputState;

public class Game extends Application{

	private Canvas canvas;
	private InputHandeler input;

	private GraphicsHandeler graphics;
	private PhysicsHandeler physics;

	private AnimationTimer updateTimer;

	private Character character;

	public Game() {

	}


	@Override
	public void init() {
		//graphics = new GraphicsHandeler();
		graphics = new GraphicsHandeler();
		physics = new PhysicsHandeler();
		updateTimer = new AnimationTimer() {
			@Override
			public void handle(long currentNanoTime) {
				update();
			}
		};
		character = new Character( 200, 200);

		canvas = graphics.init();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		graphics.start(primaryStage);
		updateTimer.start();

		graphics.addRenderable(character);

		input = new InputHandeler( canvas ); // set focus on canvas node
	}

	public void update() {

		InputState inputState = input.getState();
		character.update(inputState);
		physics.update();
		graphics.update();
	}

}
