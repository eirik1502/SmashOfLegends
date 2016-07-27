package network.ClientGameObjects;

import graphics.GraphicsEntity;
import graphics.Sprite;
import network.CharacterState;
import physics.Collideable;
import physics.PhShape;

public abstract class ClientEntity extends GraphicsEntity implements Collideable{

	
	
	
	public ClientEntity(Sprite sprite, float x, float y, float rotation) {
		super(sprite, x, y, rotation);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setCharacterState(CharacterState state) {
		setX(state.getX());
    	setY(state.getY());
    	setRotation(state.getDirection());
	}
	
	public void update() {
		sprite.update();
	}
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void addX(float x) {
		this.x += x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public void addY(float y) {
		this.y += y;
	}
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public PhShape getPhShape() {
		// TODO Auto-generated method stub
		return null;
	}
}
