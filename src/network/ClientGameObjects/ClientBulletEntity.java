package network.ClientGameObjects;

import graphics.Sprite;
import network.CharacterState;
import network.Client;
import network.NetBulletState;
import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;

import static maths.TrigUtils.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gameObjects.Enemy;

public abstract class ClientBulletEntity extends ClientEntity {

	
	private static List< Class<? extends ClientBulletEntity> > BULLET_CLASSES = new ArrayList<>();
	static {
		BULLET_CLASSES.add(ClientBasicBullet.class);
		BULLET_CLASSES.add(ClientSpecialBullet.class);
	}
	
	public static void loadSprites() {
		try {
			for (Class<? extends ClientBulletEntity> bulletClass : BULLET_CLASSES) {

				Method m = bulletClass.getMethod("loadSprite");
				m.invoke(null); //invoke static
				
			}
		}
		catch (NoSuchMethodException e) {
			System.err.println("Forgt to implement a static loading method in sub bullet class?");
			e.printStackTrace();
		}
		catch (SecurityException e) {
			
			e.printStackTrace();
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println("Something wrong with static method invocation");
			e.printStackTrace();
		}
	}
	
	
	private Client client;
	
	private float speed;
	private float radius;
	private int timer;
	
	
	public ClientBulletEntity(Client client, Sprite sprite, NetBulletState state, float radius, int timer) {
		super(sprite, 0f,0f,0f);
		this.client = client;
		super.setCharacterState(state);
		this.speed = state.getSpeed();
		this.radius = radius;
		this.timer = timer;
	}
//	public ClientBulletEntity(Client client, Sprite sprite,  float x, float y, float rotation, float speed) {
//		super(sprite, x, y, rotation);
//		this.client = client;
//		this.speed = speed;
//	}
	
	public abstract void onCharacterCollision(ClientCharacterEntity c);
	
	
	public static ClientBulletEntity getBulletByNumber(Client client, NetBulletState state) {
		ClientBulletEntity result;
		switch(state.getBulletNumber()) {
		case 0:
			result = new ClientBasicBullet(client, state);
			break;
		case 1:
			result = new ClientSpecialBullet(client, state);
			break;
		
		default:
			throw new IllegalStateException("trying to create a bullet by a number that doesnt exist");
		}
		
		return result;
	}
	
	@Override
	public void update() {
		super.update();
		
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
			onCharacterCollision(c);
		}
	}

	public void destroy() {
		client.removeBullet(this);
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
