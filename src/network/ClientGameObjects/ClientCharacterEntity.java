package network.ClientGameObjects;

import graphics.Sprite;
import network.CharacterState;
import physics.PhRectangle;
import physics.PhShape;

public class ClientCharacterEntity extends ClientEntity{

	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/frank_3_shotgun_strip13.png", 13, 32f, 32f); //40, 8);
		sprite.setImageSpeed(0);
		sprite.setOnAnimationEnd((s)->s.setImageSpeed(0));
	}
	
	
	private float radius = 32;
	
	
	public ClientCharacterEntity(float x, float y, float rotation) {
		super(sprite, x, y, rotation);
	}
	
	public void setCharacterState(CharacterState state) {
		setX(state.getX());
    	setY(state.getY());
    	setRotation(state.getDirection());
	}
	
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(getX()-radius, getY()-radius, radius*2, radius*2);
	}
	
	public void shootAnimation() {
		sprite.setImageSpeed(1);
	}
}
