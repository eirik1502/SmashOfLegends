package network.ClientGameObjects;

import graphics.Sprite;
import network.CharacterState;
import network.NetBulletState;
import network.client.Client;
import serverGame.entities.BasicBullet;

public class ClientBasicBullet extends ClientBulletEntity {

	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/special_bullet.png", 40, 8); //40, 8);
	}
	
	
//	public byte BULLET_NR = BasicBullet.BULLET_NR;
	public static float RADIUS = BasicBullet.RADIUS;
	public static int TIMER = BasicBullet.TIMER;
//	public static int DAMAGE = BasicBullet.DAMAGE;
//	public static float KNOCKBACK = BasicBullet.KNOCKBACK;
	
	
	
	public ClientBasicBullet(Client client, NetBulletState state) {
		super(client, sprite, state, RADIUS, TIMER);
		
		
	}



	@Override
	public void onCharacterCollision(ClientCharacterEntity c) {
		destroy();
		
	}

}
