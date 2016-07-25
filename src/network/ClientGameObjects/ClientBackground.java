package network.ClientGameObjects;

import graphics.Sprite;

public class ClientBackground extends ClientEntity {

	
	private static String backgroundImage = "res/background_difuse.png";
	
	
	public ClientBackground() {
		super(new Sprite(backgroundImage, 0, 0, -0.99f), 0f, 0f, 0f);
	}
	
	
}
