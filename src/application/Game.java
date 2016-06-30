package application;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;
import java.util.List;

import gameObjects.Board;
import gameObjects.Bullet;
import gameObjects.Character;
import gameObjects.Enemy;
import graphics.*;
import physics.*;
import userInput.InputHandeler;
import userInput.InputState;

public class Game {


	private InputHandeler input;

	private GraphicsHandeler graphics;
	private PhysicsHandeler physics;
	
	private long window;


	private ArrayList<Updateable> updateables = new ArrayList<>();
	private boolean updatingElements = false;
	private List<Updateable> addUpdateableBuffer = new ArrayList<>();
	private List<Updateable> removeUpdateableBuffer = new ArrayList<>();
	
	private Character character;
	public Enemy enemy;
	private Board board;
	
	
	public Game() {
		graphics = new GraphicsHandeler();
		physics = new PhysicsHandeler();

	}


	public void init() {
		
		window = graphics.init();
		input = new InputHandeler(window);
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

		character = new Character( this, 200, 300 );
		enemy = new Enemy(this, 1000, 600);
		board = new Board();
		addUnit(character);
		addUnit(enemy);
		addUnit(board);

		Bullet.loadSprite();
		
	}


	public void start() {

		graphics.start();
		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while(!glfwWindowShouldClose(window)) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				
				update();
				
				updates++;
				delta--;
			}
			
			graphics.render();
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
		}
	
		glfwDestroyWindow(window);
		glfwTerminate();

		
		
	}

	
	public void update() {
		glfwPollEvents();

		InputState inputState = input.getState();
		
		//System.out.println("mouse pos: " + inputState.getMouseX() + ", " + inputState.getMouseY() );
		
		updatingElements = true;
		for (Updateable object : updateables) {
			object.update(inputState);
		}
		updatingElements = false;
		for (Updateable unit : this.addUpdateableBuffer) {
			this.addUnit(unit);
		}
		for (Updateable unit : this.removeUpdateableBuffer) {
			this.removeUnit(unit);
		}
		this.addUpdateableBuffer.clear();
		this.removeUpdateableBuffer.clear();
		
		//fallen out of stage?
		if (! PhysicsHandeler.isCollision(character, board)) {
			character.setX(200);
			character.setY(300);
		}
		if (! PhysicsHandeler.isCollision(enemy, board)) {
			enemy.setX(1000);
			enemy.setY(600);
		}

		//physics.update();
	}

	public void addRenderable( Renderable object ) {
		graphics.addRenderable(object);
	}
	public void removeRenderable( Renderable object ) {
		graphics.removeRenderable(object);
	}

	public void addUnit( Object unit ) {
		if (unit instanceof Updateable)
			if (updatingElements) { //not added at all
				addUpdateableBuffer.add((Updateable)unit);
				return;
			}
			else
				updateables.add((Updateable)unit);
		if (unit instanceof Renderable)
			addRenderable((Renderable)unit);
	}
	public void removeUnit(Object unit) {
		if (unit instanceof Updateable) {
			if (updatingElements) { //not added at all
				removeUpdateableBuffer.add((Updateable)unit);
				return;
			}
			else
				updateables.remove((Updateable)unit);
		}
		if (unit instanceof Renderable)
			removeRenderable((Renderable)unit);
	}
	
	public Enemy getEnemy() {
		return enemy;
	}
	public Character getCharacter() {
		return character;
	}
	
	
	
	public static void main(String[] args) {
		Game game = new Game();
		game.init();
		game.start();
	}
}
