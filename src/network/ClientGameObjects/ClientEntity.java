package network.ClientGameObjects;

import game.net.CharacterState;
import graphics.GraphicsEntity;
import graphics.Sprite;
import physics.Collideable;
import physics.PhShape;

public abstract class ClientEntity extends GraphicsEntity implements Collideable{

	
	private Sprite sprite;
	
	
	public ClientEntity(Sprite sprite, float x, float y, float rotation) {
		this.sprite = sprite;
		sprite.setX(x);
		sprite.setY(y);
		sprite.setRotation(rotation);
		super.addSprite(sprite); //to be drawn
	}
	
	
	public void update() {
		sprite.update();
	}
	
	public float getX() {
		return sprite.getX();
	}
	public void setX(float x) {
		sprite.setX(x);
	}
	public void addX(float x) {
		sprite.setX(sprite.getX() + x);
	}
	public float getY() {
		return sprite.getY();
	}
	public void setY(float y) {
		sprite.setY(y);
	}
	public void addY(float y) {
		sprite.setY(sprite.getY() + y);
	}
	public float getRotation() {
		return sprite.getRotation();
	}
	public void setRotation(float rotation) {
		sprite.setRotation(rotation);
	}

	@Override
	public PhShape getPhShape() {
		// TODO Auto-generated method stub
		return null;
	}
}
