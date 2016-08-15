package serverGame;

import serverGame.entities.Board;
import serverGame.entities.Bullet;
import serverGame.entities.Character;

import java.util.ArrayList;
import java.util.LinkedList;

import graphics.Camera;
import network.server.ClientInput;
import physics.Collideable;
import physics.PhysicsHandeler;


public class ServerGame {
	
	
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	public static final int UPS = 60;
	
	
	private ArrayList<Entity> entities = new ArrayList<>();
	
	private LinkedList<Entity> addEntityBuffer = new LinkedList<>();
	private LinkedList<Entity> removeEntityBuffer = new LinkedList<>();
	protected boolean updatingEntities = false;
	
	
	//to be used by network
	private LinkedList<Bullet> createdBullets = new LinkedList<>();
	
	private Character player1, player2;
	private Camera camera1, camera2;
	
	public RelevantInputState player1InputState, player2InputState;
	//---
	
	private Board board;
	
	
	private boolean active = true;
	
	

	
	public void load() {
		player1 = new Character(298f, 435f);
		player2 = new Character(4489f, 452f);
		
		camera1 = new Camera(player1.getX(), player1.getY(), 100, 100);
		camera2 = new Camera(player2.getX(), player2.getY(), 100, 100);
		
		board = new Board();
	}
	
	public void start() {
		addEntity(player1);
		addEntity(player2);
		addEntity(board);
	}

	public void unload() {
		
	}

	
	public void update(ClientInput[] input) {
		if (!active)return;
		
		RelevantInputState[] inputStates = new RelevantInputState[input.length];
		for (int i = 0; i < input.length; i++) {
			ClientInput in = input[i];
			inputStates[i] =  new RelevantInputState(
					in.mouseX, in.mouseY, in.mvUp, in.mvDown, in.mvLeft, in.mvRight, in.ac1, in.ac2);
		}
		this.setRelevantInputState(inputStates);
		

		player1.setRelevantInputState(this.player1InputState);
		player2.setRelevantInputState(this.player2InputState);
		
		updatingEntities = true;
		
		for (Entity e : entities) {
			e.update();
		}
		
		updatingEntities = false;
		
		while(!addEntityBuffer.isEmpty()) {
			addEntity(addEntityBuffer.poll());
		}
		while(!removeEntityBuffer.isEmpty()) {
			removeEntity(removeEntityBuffer.poll());
		}
		
		
		camera1.setX(player1.getX());
		camera1.setY(player1.getY());
		camera2.setX(player2.getX());
		camera2.setY(player2.getY());
	}
	
	
	public void addEntity(Entity e) {
		if (updatingEntities) { //not added at all
			addEntityBuffer.add(e);
			return;
		}
		else {
			entities.add(e);
			e.gameInit(this);
		}
	}
	public void removeEntity(Entity e) {
		if (updatingEntities) { //not added at all
			removeEntityBuffer.add(e);
			return;
		}
		else {
			entities.remove(e);
		}
	}
	
	
	//------------------------------------------------------used by entities
	public void addBullet(Bullet bullet) {
		createdBullets.add(bullet);
		addEntity(bullet);
	}
	public void removeBullet(Bullet bullet) {
		removeEntity(bullet);
	}
	
	public Character collideCharacter(Collideable c) {
		if (PhysicsHandeler.isCollision(player1, c)) {
			return player1;
		}
		if (PhysicsHandeler.isCollision(player2, c)) {
			return player2;
		}
		return null;
	}

	public boolean collideWall(Collideable c) {
		return board.isCollisionWall(c);
	}
	public boolean collideHole(Collideable c) {
		return board.isCollisionHole(c);
	}

	
	//------------------------------------------------set/get by network
	public Camera[] getCameras() {
		Camera[] cameras = {camera1, camera2};
		return cameras;
	}
	
	
	public void setRelevantInputState(RelevantInputState[] inputStates) {
		player1InputState = inputStates[0];
		player2InputState = inputStates[1];
	}
	
	
	public Bullet[] pollCreatedBullets() {
		Bullet[] bullets = new Bullet[createdBullets.size()];
		int i = 0;
		while (!createdBullets.isEmpty()) {
			bullets[i++] = createdBullets.poll();
		}
		return bullets;
	}

}
