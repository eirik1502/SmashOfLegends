package application;

import java.util.ArrayList;

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

	private ArrayList<Updateable> updateables = new ArrayList<>();

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
		Character c = new Character( this, 200, 200 );
		addUnit(c);

		canvas = graphics.init();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		graphics.start(primaryStage);
		updateTimer.start();



		input = new InputHandeler( canvas ); // set focus on canvas node
	}

	public void update() {

		InputState inputState = input.getState();
		for (Updateable object : updateables) {
			System.out.println(object);
			object.update(inputState);
		}

		physics.update();
		graphics.update();
	}

	public void addRenderable( Renderable object ) {
		graphics.addRenderable(object);
	}
	public void removeRenderable( Renderable object ) {
		graphics.removeRenderable(object);
	}

	public void addUnit( Object unit ) {
		if (unit instanceof Updateable)
			updateables.add((Updateable)unit);
		if (unit instanceof Renderable)
			addRenderable((Renderable)unit);
	}
	
	public static void main(String[] args) {
		Game.launch("");
	}
}
