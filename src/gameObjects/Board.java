package gameObjects;

import graphics.GraphicsHandeler;
import graphics.RenderObject;
import graphics.Renderable;
import graphics.Sprite;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;

public class Board implements Renderable, Collideable{

	
	private Sprite sprite;
	
	
	public Board() {
		sprite = new Sprite("res/background.png", 0, 0, -0.9f);
	}


	@Override
	public void render(RenderObject graphics) {
		graphics.drawSprite(sprite, 0, 0, 0);
		
	}


	@Override
	public PhShape getPhShape() {
		float borderGap = 116;
		return new PhRectangle(0, borderGap, GraphicsHandeler.WIDTH, GraphicsHandeler.HEIGHT - borderGap*2);
	}
	
	
}
