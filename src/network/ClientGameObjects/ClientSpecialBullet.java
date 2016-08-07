package network.ClientGameObjects;

import gameObjects.BasicBullet;
import gameObjects.SpecialBullet;
import graphics.Sprite;
import network.NetBulletState;
import network.client.Client;

public class ClientSpecialBullet extends ClientBulletEntity {

	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/bullet_cyber.png", 40, 8); //40, 8);
	}
	
	
//	public byte BULLET_NR = SpecialBullet.BULLET_NR;
	public static float RADIUS = SpecialBullet.RADIUS;
	public static int TIMER = SpecialBullet.TIMER;
//	public static int DAMAGE = SpecialBullet.DAMAGE;
//	public static float KNOCKBACK = SpecialBullet.KNOCKBACK;
	
	
	public ClientSpecialBullet(Client client, NetBulletState state) {
		super(client, sprite, state, RADIUS, TIMER);
		
		
	}



	@Override
	public void onCharacterCollision(ClientCharacterEntity c) {
		destroy();
		
	}

}
