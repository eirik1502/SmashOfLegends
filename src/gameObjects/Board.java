package gameObjects;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import graphics.GraphicsHandeler;
import graphics.Sprite;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;
import rooms.Entity;

public class Board extends Entity {

	
	public static final int WIDTH = 1600, HEIGHT = 900;
	public static final int CELL_WIDTH = 16, CELL_HEIGHT = 16;
	public static final int GRID_WIDTH = WIDTH/CELL_WIDTH, GRID_HEIGHT = HEIGHT/CELL_HEIGHT;
	
	private Wall[][] collisionGrid = new Wall[GRID_WIDTH][GRID_HEIGHT];
	
	
	
	public Board() {
		super(new Sprite("res/background.png", 0, 0, -0.9f), 0, 0, 0);
		
		setupCollisionGrid();
	}
	
	@Override
	public void start() {
		createGridEntities();
	}
	
	public boolean collideGrid(Collideable obj) {
		PhRectangle objr = (PhRectangle)obj.getPhShape();
		float objX = objr.getX();
		float objY = objr.getY();
		float objW = objr.getWidth();
		float objH = objr.getHeight();
		return wallInGridAreaGlobal(objX, objY, objW, objH);
	}
	
	private boolean wallInGridAreaGlobal(float globalX, float globalY, float globalW, float globalH) {
		Wall[][] walls = getGridAreaGlobal(globalX, globalY, globalW, globalH);
		for (int i = 0; i < walls.length; i++) {
			for (int j = 0; j < walls[i].length; j++) {
				if (walls[i][j] != null) return true;
			}
		}
		return false;
	}
	
	private Wall[][] getGridAreaGlobal(float globalX, float globalY, float globalW, float globalH) {
		int localX1 = getCellXLocal(globalX);
		int localY1 = getCellYLocal(globalY);
		int localX2 = getCellXLocal(globalX+globalW);
		int localY2 = getCellYLocal(globalY+globalH);
		int localW = localX2-localX1;
		int localH = localY2-localY1;
		
		System.out.println("global: "+ globalX+" "+ globalY+" "+ globalW+" "+ globalH +" local: " + localX1+" "+ localY1+" "+ localW+" "+ localH);
		
		Wall[][] cells = new Wall[localW][localH];
		for (int i = 0; i < localW; i++) {
			for (int j = 0; j < localH; j++) {
				cells[i][j] = collisionGrid[i+localX1][j+localY1];
			}
		}
		return cells;
	}

//	private void setCellGlobal(boolean value, float globalX, float globalY) {
//		int cellX = getCellX(globalX);
//		int cellY = getCellY(globalY);
//		grid[cellX][cellY] = value;
//	}
//	public Boolean getCellGlobal(float globalX, float globalY) {
//		int cellX = getCellX(globalX);
//		int cellY = getCellY(globalY);
//		boolean cell = grid[cellX][cellY];
//		return cell;
//	}
	private int getCellXLocal(float globalX) {
		return (int)Math.floor(globalX / CELL_WIDTH);
	}
	private int getCellYLocal(float globalY) {
		return (int)Math.floor(globalY / CELL_HEIGHT);
	}

	private void setupCollisionGrid() {
		fillGrid(collisionGrid, null);
		int gridW = collisionGrid.length;
		int gridH;
		for (int i = 0; i<gridW; i++) {
			gridH = collisionGrid[i].length;
			for (int j = 0; j<gridH; j++) {
				if (i == 0 || i == gridW-1 || j == 0 || j == gridH-1) {
					collisionGrid[i][j] = new Wall(CELL_WIDTH*i, CELL_HEIGHT*j, CELL_WIDTH, CELL_HEIGHT);
				}
				
			}
		}
		
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


//	@Override
//	public PhShape getPhShape() {
//		float borderGap = 116;
//		return new PhRectangle(0, borderGap, Game.WIDTH, Game.HEIGHT - borderGap*2);
//	}
	
	
}
