package serverGame.entities;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import graphics.GraphicsHandeler;
import graphics.Sprite;
import physics.Collideable;
import physics.PhCircle;
import physics.PhRectangle;
import physics.PhShape;
import physics.PhysicsHandeler;
import serverGame.Entity;
import serverGame.Game;

public class Board extends Entity {

	
	public static final int WIDTH = 4800, HEIGHT = 928;
	public static final int CELL_WIDTH = 32, CELL_HEIGHT = 32;
	public static final int GRID_WIDTH = WIDTH/CELL_WIDTH, GRID_HEIGHT = HEIGHT/CELL_HEIGHT;
	
	private Wall[][] collisionGrid = new Wall[GRID_WIDTH][GRID_HEIGHT];
	private List<Hole> holelist = new ArrayList<Hole>();
	
	
	
	public Board() {
		super(0, 0, 0);
		setupHoles();
		setupCollisionGrid();
	}
	
	@Override
	public void start() {
		createGridEntities();
	}
	
	private void setupHoles(){
		//outer bound holes
		holelist.add(new Hole(new PhRectangle(0,0,WIDTH,40)));
		holelist.add(new Hole(new PhRectangle(0,850,WIDTH,20)));
		
		//on map circle holes
		holelist.add(new Hole(new PhCircle(1764f, 323f, 36f)));
		holelist.add(new Hole(new PhCircle(3173f, 582f, 36f)));
		
		//on map middle rectangle
		holelist.add(new Hole(new PhRectangle(2451f, 372f, 26f, 182f)));
	}
	
	


	private int getCellXLocal(float globalX) {
		return (int)Math.floor(globalX / CELL_WIDTH);
	}
	private int getCellYLocal(float globalY) {
		return (int)Math.floor(globalY / CELL_HEIGHT);
	}

	private void setupCollisionGrid() {
		//behind base walls
		createWallArea(0,3,		0,21);
		createWallArea(149,3,	0,21);
		
		//random walls in middle
		createWallArea(39,9,	1,9);
		createWallArea(110,9,	1,9);
		
	}
	
	private void createWallArea(int x, int y, int w, int h) {
		for (int i = x; i < x+w+1; i++) {
			for (int j = y; j < y+h+1; j++) {
				createWallCell(i, j);
			}
		}
	}
	
	private void createWallCell(int i, int j) {
		collisionGrid[i][j] = new Wall(CELL_WIDTH*i, CELL_HEIGHT*j, CELL_WIDTH, CELL_HEIGHT);
	}
	
	private void createGridEntities() {
		int gridW = collisionGrid.length;
		int gridH;
		for (int i = 0; i<gridW; i++) {
			gridH = collisionGrid[i].length;
			for (int j = 0; j<gridH; j++) {
				Wall w = collisionGrid[i][j];
				if (w != null) {
					room.addEntity(w);
				}
				
			}
		}
	}
	private void fillGrid(Wall[][] grid, Wall value) {
		for (int i = 0; i<grid.length; i++) {
			for (int j = 0; j<grid[i].length; j++) {
				grid[i][j] = value;
			}
		}
	}
	
	public boolean isCollisionHole(Collideable c) {
		for (Hole h: holelist){
			if (PhysicsHandeler.isCollision(h,c)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isCollisionWall(Collideable c) {
		PhRectangle rect = c.getPhShape().getBoundingBox();
		Wall[][] walls = getGridAreaGlobal(rect);
		for (int i = 0; i < walls.length; i++) {
			for (int j = 0; j < walls[i].length; j++) {
				if (walls[i][j] != null && PhysicsHandeler.isCollision(c, walls[i][j])) {
					return true; 
				}
			}
		}	
		return false;
	}
			
	
	private Wall[][] getGridAreaGlobal(PhRectangle rect) {
		float rectX = rect.getX();
		float rectY = rect.getY();
		float rectW = rect.getWidth();
		float rectH = rect.getHeight();
		int localX1 = getCellXLocal(rectX);
		int localY1 = getCellYLocal(rectY);
		int localX2 = getCellXLocal(rectX+rectW);
		int localY2 = getCellYLocal(rectY+rectH);
		int localW = localX2-localX1;
		int localH = localY2-localY1;
		
		//System.out.println("global: "+ rectX+" "+ rectY+" "+ rectW+" "+ rectH +" local: " + localX1+" "+ localY1+" "+ localW+" "+ localH);
		try {
			Wall[][] cells = new Wall[localW][localH];
			for (int i = 0; i < localW; i++) {
				for (int j = 0; j < localH; j++) {
					cells[i][j] = collisionGrid[i+localX1][j+localY1];
				}
			}
			return cells;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			Wall[][] walls = new Wall[0][0];
			return walls;
		}
	}

//	@Override
//	public PhShape getPhShape() {
//		float borderGap = 116;
//		return new PhRectangle(0, borderGap, Game.WIDTH, Game.HEIGHT - borderGap*2);
//	}
	
	
}
