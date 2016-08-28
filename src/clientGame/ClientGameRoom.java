package clientGame;

import clientGame.net.ClientGameNet;

public class ClientGameRoom extends NetRoom {


	private ClientGameNet gameNet;
	
	
	public ClientGameRoom() {
		super(ClientMain.WINDOW_WIDTH, ClientMain.WINDOW_HEIGHT);
	}
	
	
	@Override
	public void load() {
		
	}

	@Override
	public void start() {
		gameNet = new ClientGameNet( super.getServerConnection() );
		
	}

	@Override
	public void stop() {
		gameNet = null;
		
	}

	@Override
	public void unload() {
		
		
	}

	
}
