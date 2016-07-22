package network.ClientGameObjects;

import graphics.Sprite;
import physics.PhRectangle;
import physics.PhShape;

public class ClientCharacterEntity extends ClientEntity{

	
	private static Sprite sprite;
	
	
	private float radius = 32;
	
	public static void loadSprite() {
		sprite = new Sprite("res/guy.png", 32, 32); //40, 8);
	}
	
	
	public ClientCharacterEntity(float x, float y, float rotation) {
		super(sprite, x, y, rotation);
	}
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}
}
