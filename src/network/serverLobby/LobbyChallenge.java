package network.serverLobby;

public class LobbyChallenge {

	
	private ConnectedClient client1, client2;
	private boolean client1Accept, client2Accept;
	
	
	public LobbyChallenge(ConnectedClient client1, ConnectedClient client2) {
		this.client1 = client1;
		this.client2 = client2;
		client1Accept = false;
		client2Accept = false;
	}
	
	public ConnectedClient getClient1() {
		return client1;
	}
	public ConnectedClient getClient2() {
		return client2;
	}
	
	public void clientAccept(ConnectedClient client) {
		if (client1 == client) client1Accept = true;
		else if (client2 == client) client2Accept = true;
		else throw new IllegalArgumentException("id given did not match any registered id in this object");
	}
	public boolean contains(ConnectedClient client1, ConnectedClient client2) {
		if ( (client1 == this.client1 && client2 == this.client2) ||
				(client1 == this.client2 && client2 == this.client1)) {
			return true;
		}
		return false;
	}
	public boolean containes(ConnectedClient client) {
		if (client == client1 || client == client2) {
			return true;
		}
		return false;
	}
	public ConnectedClient getOther( ConnectedClient client) {
		if (client == client1) return client2;
		if (client == client2)  return client1;
		return null;
	}
	
	public boolean singleAccept() {
		return client1Accept || client2Accept;
	}
	public boolean ready() {
		return client1Accept && client2Accept;
	}
	
}
