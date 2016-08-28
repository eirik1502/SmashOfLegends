package clientGame;

import network.baseConnection.Host;
import network.baseConnection.SingleHostNet;
import rooms.Room;

public abstract class NetRoom extends Room {

	public NetRoom(float width, float height) {
		super(width, height);
	}
	private static SingleHostNet serverConnection = null;
	
	
	public boolean createServerConnection(Host serverHost) {
		SingleHostNet connection = SingleHostNet.createServerConnection(serverHost);
		if (connection != null) {
			setServerConnection(connection);
			return true;
		}
		return false;
	}
	public void setServerConnection(SingleHostNet connection) {
		serverConnection = connection;
	}
	public void removeServerConnection() {
		serverConnection = null;
	}
	public boolean isServerConnection() {
		return serverConnection != null;
	}
	public SingleHostNet getServerConnection() {
		if (serverConnection == null) throw new IllegalStateException("Set server connection before trying to use it");
		return serverConnection;
	}
}
