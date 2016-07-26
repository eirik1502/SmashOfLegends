package game;

import rooms.RelevantInputState;
import rooms.Room;
import rooms.Text;
//import trash.GrCircle;
//import trash.GrRectangle;
//import trash.ShapeRenderer;

import java.util.ArrayList;
import java.util.LinkedList;

import gameObjects.Board;
import gameObjects.Bullet;
import gameObjects.Character;
import gameObjects.Enemy;
import gameObjects.Wall;
import graphics.Camera;
import graphics.Color;
import graphics.Font;
import maths.TrigUtils;
import physics.Collideable;
import physics.PhysicsHandeler;

public class GameRoom extends Room {
	
	
	private LinkedList<Bullet> createdBullets = new LinkedList<>();
	
	
	private Character player1, player2;
	private Camera camera1, camera2;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private Board board;
	
	private Text text;
	
	
	private int genEnemyInterval = 60*15;
	private int genEnemyTimer = 0;
	private int enemyWave = 0;
	
	private boolean active = true;
	
	public RelevantInputState player1InputState, player2InputState;

	
	@Override
	public void load() {
//		Bullet.loadSprite();
//		Enemy.loadSprite();
//		Wall.loadSprite();
		player1 = new Character(300f, 300f);
		player2 = new Character(1200f, 300f);
		
		camera1 = new Camera(player1.getX(), player1.getY(), 100, 100);
		camera2 = new Camera(player2.getX(), player2.getY(), 100, 100);
		player1.setCamera(camera1);
		player2.setCamera(camera2);
		//board = new Board();
		
		board = new Board();
		
		//text = new Text("Heello world!", Font.getStandardFont(), 18, 200, 70, 0);
				

	}
	
	@Override
	public void start() {
		super.addEntity(player1);
		super.addEntity(player2);
		//genEnemy();
		super.addEntity(board);
//		super.addText(text);
//		super.addEntity(board);
//		super.addEntity(rect1);
//		super.addEntity(circ1);
	}

	@Override
	public void stop() {
		//super.removeEntity(player);
//		super.removeEntity(rect1);
//		super.removeEntity(circ1);
	}

	@Override
	public void unload() {
		//player = null;
		
	}
	
	@Override
	public void update() {
		if (!active)return;
//		if (genEnemyTimer-- == 0) {
//			enemyWave++;
//			for(int i = 0; i < enemyWave; i++) {
//				genEnemy();
//			}
//			genEnemyTimer = this.genEnemyInterval;
//		}
//		text.setString("Wave: " + enemyWave);
		player1.update(this.player1InputState);
		player2.update(this.player2InputState);
		camera1.setX(player1.getX());
		camera1.setY(player1.getY());
		camera2.setX(player2.getX());
		camera2.setY(player2.getY());
		
		super.update();
	}
	
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
	
	public void addBullet(Bullet bullet) {
		createdBullets.add(bullet);
		addEntity(bullet);
	}
	public void removeBullet(Bullet bullet) {
		removeEntity(bullet);
	}
//	
//	private void genEnemy() {
//		float[] points = {0,0,   Game.WIDTH, 0,   0, Game.HEIGHT,   Game.WIDTH, Game.HEIGHT};
//		int pickPoint = 2 * (int)Math.floor(Math.random()*4);
//		float startX = points[pickPoint];
//		float startY = points[pickPoint+1];
//		Enemy newEnemy = new Enemy(startX, startY);
//		enemies.add(newEnemy);
//		addEntity(newEnemy);
//	}
	
//	public Character getPlayer() {
//		return player;
//	}
//	public ArrayList<Enemy> getEnemies() {
//		return enemies;
//	}
//	public void  removeEnemy(Enemy e) {
//		enemies.remove(e);
//		removeEntity(e);
//	}
//	private void removePlayer() {
//		removeEntity(player);
//		
//	}
	public Character collideCharacter(Collideable c) {
		if (PhysicsHandeler.isCollision(player1, c)) {
			return player1;
		}
		if (PhysicsHandeler.isCollision(player2, c)) {
			return player2;
		}
		return null;
	}
//	public Enemy collideEnemy(Collideable c) {
//		for (Enemy e : enemies) {
//			if (PhysicsHandeler.isCollision(c, e)) {
//				return e;
//			}
//		}
//		return null;
//	}
	public boolean collideWall(Collideable c) {
		return board.isCollisionWall(c);
	}
	public boolean collideHole(Collideable c) {
		return board.isCollisionHole(c);
	}

	
//	public void gameOver() {
//		enemies.forEach(e -> removeEntity(e));
//		enemies.clear();
//		
//		Text t = new Text("You lost! reached wave: " + Integer.toString(this.enemyWave), Font.getStandardFont(), 18, player.getX(), player.getY(), 0f);
//		addText(t);
//		removeEntity(player);
//		active = false;
//	}

}
