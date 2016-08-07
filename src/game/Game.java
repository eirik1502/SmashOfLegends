package game;

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

import game.GameRoom;
import gameObjects.Board;
import gameObjects.Bullet;
import gameObjects.Character;
import gameObjects.Enemy;
import graphics.*;
import network.client.Client;
import network.server.ClientInput;
import physics.*;
import rooms.Entity;
import rooms.RelevantInputState;
import rooms.Room;
import rooms.RoomHandeler;
import rooms.Text;
import userInput.InputHandeler;
import userInput.InputState;

public class Game {

	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	public static final int UPS = 60;
	

	
	
	private PhysicsHandeler physicsHandeler;
	
	//private RoomHandeler roomHandeler;
	
	private GameRoom gameRoom;

	
	
	
	public Game() {

	}


	public void init() {
		
		//physicsHandeler = new PhysicsHandeler();
		
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

		//roomHandeler = new RoomHandeler( );
		//ArrayList<Room> rooms = new ArrayList<>();
		//rooms.add(new GameRoom());
//		rooms.add(new IntroRoom());
//		rooms.add(new MenuRoom());
		//rooms.add(new GameRoom());
		//roomHandeler.setRooms(rooms);
		gameRoom = new GameRoom();
		gameRoom.load();
		
		
	}


	public void start() {

		gameRoom.start();
		
//		long lastTime = System.nanoTime();
//		double delta = 0.0;
//		double ns = 1000000000.0 / 60.0;
//		long timer = System.currentTimeMillis();
//		int updates = 0;
//		int frames = 0;
//		
//		while(true) {//!glfwWindowShouldClose(window)) {
//			
//			long now = System.nanoTime();
//			delta += (now - lastTime) / ns;
//			lastTime = now;
//			if (delta >= 1.0) {
//				
//				update();
//				
//				updates++;
//				delta--;
//			}
//			
//			//no render
//			
//			frames++;
//			if (System.currentTimeMillis() - timer > 1000) {
//				timer += 1000;
//				System.out.println(updates + " ups, " + frames + " fps");
//				updates = 0;
//				frames = 0;
//			}
//		}
		
	
		//glfwDestroyWindow(window);
		//glfwTerminate();

		
		
	}

	
	public void update(ClientInput[] input) {
		//glfwPollEvents();
		RelevantInputState[] inputStates = new RelevantInputState[input.length];
		for (int i = 0; i < input.length; i++) {
			ClientInput in = input[i];
			inputStates[i] =  new RelevantInputState(
					in.mouseX, in.mouseY, in.mvUp, in.mvDown, in.mvLeft, in.mvRight, in.ac1, in.ac2);
		}
		gameRoom.setRelevantInputState(inputStates);
		gameRoom.update();
	}

	
	public Camera[] getCameras() {
		return gameRoom.getCameras();
	}
	public Entity[] getEntities() {
		return gameRoom.getEntities();
	}
	
	public Bullet[] pollCreatedBullets() {
		return gameRoom.pollCreatedBullets();
	}
//	public void render() {
//		Entity[] entities = roomHandeler.getEntities();
//		Text[] texts = roomHandeler.getTexts();
//		graphicsHandeler.render(entities, texts);
//	}
	
	
	
//	public static void main(String[] args) {
//		Game game = new Game();
//		game.init();
//		game.start();
//	}
}
