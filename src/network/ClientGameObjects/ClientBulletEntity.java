package network.ClientGameObjects;

import graphics.Sprite;
import network.CharacterState;
import network.Client;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;

import static maths.TrigUtils.*;

import gameObjects.Enemy;

public class ClientBulletEntity extends ClientEntity {

	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/bullet_cyber.png", 40, 8); //40, 8);
	}
	
	
	private Client client;
	
	private float speed;
	private float radius = 1;
	
	private int timer = 60;
	
	
	public ClientBulletEntity(Client client, CharacterState state, float speed) {
		super(sprite, 0f,0f,0f);
		this.client = client;
		super.setCharacterState(state);
		this.speed = speed;
	}
	public ClientBulletEntity(Client client, float x, float y, float rotation, float speed) {
		super(sprite, x, y, rotation);
		this.client = client;
		this.speed = speed;
	}
	
	@Override
	public void update() {
		if (timer-- == 0) client.removeBullet(this);
		
		x += Math.cos(rotation)*speed;
		y += Math.sin(rotation)*speed;
		
		checkCharacterHit();
//		addX( lengthdirX(rotation, speed) );
//		addY( lengthdirY(rotation, speed) );
		
		//Enemy enemy = groom.collideEnemy(this);
	}
	private void checkCharacterHit() {
		ClientCharacterEntity c = client.collideCharacter(this);
		if (c != null) {
			client.removeBullet(this);
		}
	}

	public void destroy() {
		
	}
	
	public float getSpeed() {
		return speed;
	}
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}
	
	
	public String toString() {
		return "[ClientBullet, x: "+getX()+" y: "+getY()+" dir: "+getRotation()+" speed: "+getSpeed()+"]";
	}
	
}
