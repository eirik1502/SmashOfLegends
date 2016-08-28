package server;

import java.util.ArrayList;

import network.baseConnection.JoinRequestHandeler;
import network.baseConnection.SingleHostNet;
import network.server.ClientInput;
import network.server.Server;
import serverGame.ServerGame;
import serverGame.net.ServerGameNet;

public class ServerMain {

	
	public static int SERVER_CONNECT_PORT = 7770;
			
	
	private boolean running = true;
	private JoinRequestHandeler connectionHandeler;
	private ServerGame game;
	private ServerGameNet gameNet;
	
	private ArrayList<SingleHostNet> connections = new ArrayList<>(4);
	
	private boolean ingame = false;
	
	
	public static void main(String[] args) {
		ServerMain main = new ServerMain();
		main.init();
		main.start();
	}
	
	public void init() {
		connectionHandeler = new JoinRequestHandeler(ServerMain.SERVER_CONNECT_PORT);
		connectionHandeler.setListener(connection -> onConnection(connection));
		
	}
	public void start() {
		connectionHandeler.start();
		loop();
		onTermination();
	}
	
	public void terminate() {
		running = false;
	}
	private void onTermination() {
		connectionHandeler.terminate();
		for (SingleHostNet c : connections) {
			c.terminate();
		}
	}
	
	private void startGame() {
		game = new ServerGame();
		gameNet = new ServerGameNet(connections.get(0), connections.get(1));
		
		game.load();
		game.start();
		
		ingame = true;
	}
	private void onConnection(SingleHostNet connection) {
		System.out.println("Client trying to connect");
		if (connections.size() < 2) {
			System.out.println("Client accepted");
			connections.add(connection);
		}
		else System.err.println("Client trying to join was denied, server full");
		
		if (connections.size() == 2) {
			System.out.println("Starting game");
			startGame();
		}
	}
	
	private void update() {
		if (ingame) {
			ClientInput[] inputs = gameNet.pollClientsInput();
			game.update(inputs);
		}
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
