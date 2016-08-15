package network.clientLobby;

import network.ChatMessage;
import network.baseConnection.SingleHostNet;
import org.json.simple.*;

public class ConnectedClientImage {

	
	private ClientLobbyNet lobbyNet;
	private int clientNumber;
	private final String name;
	
	//private ConnectedClientImageListener challengeRequestListener, challengeAcceptListener, challengeDeclineListener, chatMessageListner;
	
	public ConnectedClientImage( int clientNumber, String name) {
		//this.lobbyNet = lobbyNet;
		this.clientNumber = clientNumber;
		this.name = name;
		
	}
	
	public void requestChallenge() {
		lobbyNet.sendChallengeRequest(this);
	}
	public void acceptChallenge() {
		lobbyNet.sendChallengeResponse(this, true);
	}
	public void declineChallenge() {
		lobbyNet.sendChallengeResponse(this, false);
	}
	
	
	public void receiveChallengeRequest() {
		
	}
	public void receiveChallengeAccept() {
		
	}
	public void receiveChallengeDecline() {
		
	}
	public void receiveChatMessage(ChatMessage msg) {
		
	}
	
	public int getId() {
		return clientNumber;
	}
	public String getName() {
		return name;
	}
	
	private void onNetMessage(JSONObject msg) {
		
	}

}
