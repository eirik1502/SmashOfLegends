package clientGame;

import network.baseConnection.Host;
import network.server.Server;

public class ConnectionRoom extends NetRoom {
	
	
	private final Host serverHost = new Host("", Server.PORT_NUMBER, -1);
	

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		if (super.getServerConnection() == null) {
			super.createServerConnection(serverHost);
		}
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}
	
	

}
