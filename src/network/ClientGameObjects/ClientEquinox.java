package network.ClientGameObjects;

import graphics.Sprite;
import network.client.Client;
import physics.PhRectangle;
import physics.PhShape;

public class ClientEquinox extends ClientEntity {

	
	private static Sprite sprite;
	public static void loadSprite() {
		sprite = new Sprite("res/Equinox.png", 60, 41); //40, 8);
	}
	
	
	private Client client;
	private float radius = 40;
	
	
	public ClientEquinox(Client client, float x, float y) {
		super(sprite, x, y, (float)Math.random()*6);
		this.client = client;
	}
	
	@Override
	public void update() {
		checkCharacterHit();
	}
	
	public void onCharacterCollision(ClientCharacterEntity c) {
		destroy();
		
	}
	
	private void checkCharacterHit() {
		ClientCharacterEntity c = client.collideCharacter(this);
		if (c != null) {
			onCharacterCollision(c);
		}
	}
	
	private void destroy() {
		client.removeEquinox(this);
	}
	
	@Override
	public PhShape getPhShape() {
		return new PhRectangle(x-radius, y-radius, radius*2, radius*2);
	}

}
