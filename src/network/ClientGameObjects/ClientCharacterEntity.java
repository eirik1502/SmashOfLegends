package network.ClientGameObjects;

import graphics.Sprite;
import physics.PhRectangle;
import physics.PhShape;

public class ClientCharacterEntity extends ClientEntity{

	
	private static Sprite sprite;
	
	
	private float radius = 32;
	
	public static void loadSprite() {
		sprite = new Sprite("res/frank_3_shotgun_strip13.png", 13, 32f, 32f, 0); //40, 8);
		sprite.setImageSpeed(0);
		sprite.setOnAnimationEnd((s)->s.setImageSpeed(0));
	}
	
	
	public ClientCharacterEntity(float x, float y, float rotation) {
		super(sprite, x, y, rotation);
	}
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}
	
	public void shootAnimation() {
		sprite.setImageSpeed(1);
	}
}
