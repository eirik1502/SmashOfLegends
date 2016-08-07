package network;

import network.baseConnection.SingleHostNet;
import org.json.simple.*;

public class ConnectedClient {

	
	private ClientLobbyNet lobbyNet;
	private int clientNumber;
	private final String name;
	
	
	public ConnectedClient(ClientLobbyNet lobbyNet, int clientNumber, String name) {
		this.lobbyNet = lobbyNet;
		this.clientNumber = clientNumber;
		this.name = name;
		
		
	}
	
	public void requestChallenge() {
		
	}
	public void declineChallenge() {
		
	}
	
	public int getNumber() {
		return clientNumber;
	}
	public String getName() {
		return name;
	}
	
	private void onNetMessage(JSONObject msg) {
		
	}

}
