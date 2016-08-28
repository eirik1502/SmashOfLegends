package clientGame;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;

import graphics.GraphicsHandeler;
import graphics.GraphicsUtils;
import rooms.Room;
import rooms.RoomHandeler;
import userInput.InputHandeler;

public class ClientMain {

	
	public static final int WINDOW_WIDTH = 1600, WINDOW_HEIGHT = 900;
	
	private GraphicsHandeler graphicsHandeler;
	
	private RoomHandeler roomHandeler;
	private InputHandeler inputHandeler;
	
	private boolean running = true;
	
	public static void main(String[] args) {
		ClientMain game = new ClientMain();
		game.init();
		game.start();
	}
	
	public void init() {
		long window = GraphicsUtils.createWindowOpenGl(WINDOW_WIDTH, WINDOW_HEIGHT, "Smash of Legends");
		graphicsHandeler = new GraphicsHandeler(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		inputHandeler = new InputHandeler(window);
		
		roomHandeler = new RoomHandeler();
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add( new ConnectionRoom() );
		rooms.add( new ClientGameRoom() );
		roomHandeler.setRooms(rooms);
	}
	
	public void start() {
		roomHandeler.start();
		
		loop();
	}
	public void terminate() {
		running = false;
	}
	
	private void update() {
		glfwPollEvents(); //to update input manager
		if (graphicsHandeler.windowCloseRequest()) {
			terminate();
		}
		
		roomHandeler.update();
	}
	private void render() {
		//graphicsHandeler.render(roomHandeler.getCamera(), roomHandeler.getGraphicsEntities());
		graphicsHandeler.render(roomHandeler.getGraphicsEntities());
	}
	
	private void loop() {
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while(running) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) { //1/60 of a second has past
				
				update();
				
				updates++;
				delta--;
			}
            
			render();
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) { //1 second has past
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps"+ "---------------------------------------");
				updates = 0;
				frames = 0;
			}
		}

	}
	
}
