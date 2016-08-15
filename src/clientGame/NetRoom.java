package clientGame;

import network.baseConnection.Host;
import network.baseConnection.SingleHostNet;
import rooms.Room;

public abstract class NetRoom extends Room {

	private static SingleHostNet serverConnection = null;
	
	
	public void createServerConnection(Host serverHost) {
		setServerConnection(SingleHostNet.createServerConnection(serverHost));
	}
	public void setServerConnection(SingleHostNet connection) {
		serverConnection = connection;
	}
	public void removeServerConnection() {
		serverConnection = null;
	}
	public SingleHostNet getServerConnection() {
		if (serverConnection == null) throw new IllegalStateException("Set server connection before trying to use it");
		return serverConnection;
	}
}
