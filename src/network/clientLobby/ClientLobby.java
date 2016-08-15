package network.clientLobby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import graphics.GraphicsHandeler;
import graphics.Text;
import network.ChatMessage;
import network.baseConnection.SingleHostNet;

public class ClientLobby implements ClientLobbyNetInput{

	
	
	private HashMap<Integer, ConnectedClientImage> connectedClients = new HashMap<>();
	//private ArrayList<ConnectedClientImage> connectedClients = new ArrayList<>();
	private ChatMessageContainer chatContainer;
	
	private ClientLobbyNet lobbyNet;
	
	private ArrayList<ConnectedClientImage> challengedBy = new ArrayList<>();
	private ArrayList<ConnectedClientImage> challenging = new ArrayList<>();
	
	private ConnectedClientImage challengeAccepted = null;
	private boolean gotoGame = false;
	
	
	public ClientLobby(SingleHostNet connection) {
		
		lobbyNet = new ClientLobbyNet(connection, this);
	}
	
	public ClientLobbyNet getLobbyNet() {
		return this.lobbyNet;
	}
	
	
	
	public void update() {
		lobbyNet.pollInData(); //calls the receives methods
	}
	
	public Text[] getGraphicsTexts() {
		return chatContainer.getTexts();
	}
	
	public Collection<ConnectedClientImage> getJoinedClients() {
		return this.connectedClients.values();
	}
	public ChatMessageContainer getChatContainer() {
		return this.chatContainer;
	}
	

	private ConnectedClientImage getConnectedClient(int id) {
		ConnectedClientImage client = connectedClients.get(id);
		if (client == null) {
			System.err.println("Trying to retreive a client from a non-registered id");
			return new ConnectedClientImage(id, "A mysterius non-connected client");
		}
		else {
			return client;
		}
	}
	
	private boolean isChallengedBy(ConnectedClientImage client) {
		return (challengedBy.contains(client));
	}
	private boolean isChallenging(ConnectedClientImage client) {
		return (challenging.contains(client));
	}
	private void removeChallengedBy(ConnectedClientImage client) {
		challengedBy.remove(client);
	}
	private void removeChallenging(ConnectedClientImage client) {
		challenging.remove(client);
	}
	
	
	public void joinLobby(String name) {
		lobbyNet.sendJoinLobbyRequest(name);
	}
	public void publishChatMessage(ChatMessage chatMsg) {
		lobbyNet.sendChatMessage(chatMsg.getString());
	}
	public boolean requestChallenge(ConnectedClientImage client) {
		if (isChallengedBy(client)) {
			System.err.println("cannot challenge someone that is currently challenging us");
			return false;
		}
		if (isChallenging(client)) {
			System.err.println("cannot challenge someone that is currently being challenged");
			return false;
		}
		lobbyNet.sendChallengeRequest(client.getId());
		return true;
	}
	public boolean cancelChallenge(ConnectedClientImage client) {
		if (challengeAccepted != null && challengeAccepted == client) {
			//cannot cancel an accepted challenge
			return false;
		}
		if (!isChallenging(client)) {
			System.err.println("Cannot cancel a non active challenge request");
			return false;
		}
		removeChallenging(client);
		return true;
	}
	public boolean acceptChallenge(ConnectedClientImage requestingClient) {
		if (challengeAccepted != null) {
			System.err.println("if we have accepted a challenge, cannot accept another challenge");
			return false;
		}
		if (!isChallengedBy(requestingClient)) {
			System.err.println("Cannot accept challenge from non challenging client");
			return false;
		}
		challengeAccepted = requestingClient;
		lobbyNet.sendChallengeResponse(requestingClient.getId(), true);
		return true;
	}
	public boolean declineChallenge(ConnectedClientImage requestingClient) {
		if (challengeAccepted != null && challengeAccepted == requestingClient) {
			//cannot decline already accepted challenge
			return false;
		}
		if (!isChallengedBy(requestingClient)) {
			//cannot decline a challenge that doesnt exist
			return false;
		}
		removeChallengedBy(requestingClient);
		lobbyNet.sendChallengeResponse(requestingClient.getId(), false);
		return true;
	}

	
	@Override
	public void receiveChatMessage(int senderId, String msg) {
		ChatMessage chatMsg = new ChatMessage(getConnectedClient(senderId), msg);
		chatContainer.addMessage(chatMsg);
	}
	@Override
	public void receiveChallengeRequest(int senderId, String msg) {
		//not using challenge message by now
		challengedBy.add(getConnectedClient(senderId) );
	}
	@Override
	public void receiveChallengeResponse(int senderId, boolean accept) {
		ConnectedClientImage sender = getConnectedClient(senderId);

		if (!accept) {
			if (challengeAccepted == sender) {
				challengeAccepted = null; //reset state, don't need to send anything
				gotoGame = false;
			}
			else {
				
			}
			if (isChallengedBy(sender)) removeChallengedBy(sender);
			if (isChallenging(sender)) removeChallenging(sender);
			return;
		}
		//accept == true
		if (challengeAccepted == null) { //not in closed state
			
			if (isChallengedBy(sender)) { //can't get second challenge response from someone that is not an accepted client
				System.err.println("got second challenge response from someone that is not an accepted client, weird");
			}
			else if (isChallenging(sender)) { //got first response, send one back. Threeway handshake
				challengeAccepted = sender; //lock state
				lobbyNet.sendChallengeResponse(senderId, true);
			}
			else {
				System.out.println("Declined challenge before true request came");
				//send false response
				lobbyNet.sendChallengeResponse(senderId, false);
			}
		}
		else { //challenge is already accepted, should get a response
			if (sender == challengeAccepted) { //if responder is the one we are waiting for
				if (!isChallengedBy(sender)) System.err.println("challengeAccepted does not exist as a challengedBy, sfalse state");
				System.out.println("Got second challengeResponse, waiting to receive gotoGameRequest");
				gotoGame = true;
			}
			else { //got challenge response from not accepted client
				if (isChallengedBy(sender)) {
					System.err.println("Got third response from client not in challengeAccept, should not happen");
				}
				else if (isChallenging(sender)) { //got the first response, but have to decline because we are paired with soeone else
					lobbyNet.sendChallengeResponse(senderId, false);
					removeChallengedBy(sender); //remove client from challenges
				}
				
			}
		}
	}
	@Override
	public void receiveJoinLobbyResponse(int statusCode) {
		System.out.println("We joined lobby!");
	}
	@Override
	public void receiveClientConnected(int clientId, String name) {
		ConnectedClientImage client = new ConnectedClientImage(clientId, name);
		connectedClients.put(clientId, client);
	}
	@Override
	public void receiveClientDisconnected(int clientId) {
		connectedClients.remove(clientId);
	}
	@Override
	public void receiveGotoGameRequest(int opponentClientId) {
		System.out.println("Should now goto game");
		
	}
	

}
