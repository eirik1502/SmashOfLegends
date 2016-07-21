package game;

import rooms.Room;
import rooms.Text;
//import trash.GrCircle;
//import trash.GrRectangle;
//import trash.ShapeRenderer;

import java.util.ArrayList;

import gameObjects.Board;
import gameObjects.Bullet;
import gameObjects.Character;
import gameObjects.Enemy;
import gameObjects.Wall;
import graphics.Color;
import graphics.Font;
import physics.Collideable;
import physics.PhysicsHandeler;

public class GameRoom extends Room {
	
	
	private Character player;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private Board board;
	
	private Text text;
	
	
	private int genEnemyInterval = 60*15;
	private int genEnemyTimer = 0;
	private int enemyWave = 0;
	
	private boolean active = true;
	
	@Override
	public void load() {
//		Bullet.loadSprite();
//		Enemy.loadSprite();
//		Wall.loadSprite();
		player = new Character(300f, 300f);
		//board = new Board();
		
		//text = new Text("Heello world!", Font.getStandardFont(), 18, 200, 70, 0);
				

	}
	
	@Override
	public void start() {
		super.addEntity(player);
//		super.addEntity(board);
//		super.addText(text);
//		super.addEntity(board);
//		super.addEntity(rect1);
//		super.addEntity(circ1);
	}

	@Override
	public void stop() {
		super.removeEntity(player);
//		super.removeEntity(rect1);
//		super.removeEntity(circ1);
	}

	@Override
	public void unload() {
		player = null;
		
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
		super.update();
	}
	
	private void genEnemy() {
		float[] points = {0,0,   Game.WIDTH, 0,   0, Game.HEIGHT,   Game.WIDTH, Game.HEIGHT};
		int pickPoint = 2 * (int)Math.floor(Math.random()*4);
		float startX = points[pickPoint];
		float startY = points[pickPoint+1];
		Enemy newEnemy = new Enemy(startX, startY);
		enemies.add(newEnemy);
		addEntity(newEnemy);
	}
	
	public Character getPlayer() {
		return player;
	}
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	public void  removeEnemy(Enemy e) {
		enemies.remove(e);
		removeEntity(e);
	}
//	private void removePlayer() {
//		removeEntity(player);
//		
//	}
	public Character collidePlayer(Collideable c) {
		if (PhysicsHandeler.isCollision(player, c)) {
			return player;
		}
		return null;
	}
	public Enemy collideEnemy(Collideable c) {
		for (Enemy e : enemies) {
			if (PhysicsHandeler.isCollision(c, e)) {
				return e;
			}
		}
		return null;
	}
	public boolean collideBoard(Collideable c) {
		return board.collideGrid(c);
	}
	
	public void gameOver() {
		enemies.forEach(e -> removeEntity(e));
		enemies.clear();
		
		Text t = new Text("You lost! reached wave: " + Integer.toString(this.enemyWave), Font.getStandardFont(), 18, player.getX(), player.getY(), 0f);
		addText(t);
		removeEntity(player);
		active = false;
	}

}
