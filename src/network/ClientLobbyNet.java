package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;
import rooms.Text;


public class ClientLobbyNet {


	private SingleHostNet serverConnection;
	private NetOutData outData;
	
	private HashMap<Integer, ConnectedClient> connectedClients = new HashMap<>();
	
	private ConcurrentLinkedDeque<ConnectedClient> challengeRequests = new ConcurrentLinkedDeque<>();
	private ConcurrentLinkedDeque<ChatMessage> chatMessages = new ConcurrentLinkedDeque<>();
	
	
	private boolean lobbyJoined = false; //set when received joinLobbyResponse. Will not handle other messages before set.
	private boolean challengeAccepted = false; //set when challengeResponse is sendt
	private boolean gotoGame = false; //set when challengeResponse is received after it is sendt, and accepted == true
	
	
	public ClientLobbyNet(SingleHostNet serverConnection) {
		this.serverConnection = serverConnection;
		serverConnection.setTcpDataInListener((data) -> onTcpData(data));
		serverConnection.setUdpDataInListener((data)-> {throw new IllegalArgumentException("Should not receive udp data in lobby");});
		outData = new NetOutData();
	}
	
	
	public boolean challengeRequestWaiting() {
		return challengeRequests.isEmpty();
	}
	public ConnectedClient pollChallengeRequest() {
		return challengeRequests.poll();
	}
	public boolean chatMessageWaiting() {
		return chatMessages.isEmpty();
	}
	public ChatMessage pollChatMessage() {
		return chatMessages.poll();
	}
	
	public boolean joinLobby(String name) {
		if (lobbyJoined) {
			return true;
		}
			
		sendJoinLobbyRequest(name);
		//block til lobby is joined
		try {
			while(!lobbyJoined) {
				Thread.sleep(100);
			}

		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Lobby joined");
		return true;
		
	}
	
	
	//type - name(string)
	public void sendJoinLobbyRequest(String name) {
		outData.clear();
		writeMessageType(outData, LobbyNet.JOIN_LOBBY_REQUEST);
		outData.writeString(name);
		serverConnection.sendTcpData(outData);
	}
	
	//type - msg(string)
	public void sendChatMessage(String msg) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHAT_MESSAGE_ALL);
		outData.writeString(msg);
		serverConnection.sendTcpData(outData);
	}
	
	//type - targetClient(int) - challengeType(int) - challengeMsg(string)
	public void sendChallengeRequest(ConnectedClient client) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_REQUEST);
		outData.writeInt(client.getNumber());
		outData.writeInt(0);
		outData.writeString("");
		serverConnection.sendTcpData(outData);
	}
	
	//type - tagetClient(int) - accepted(boolean)
	public void sendChallengeResponse(ConnectedClient client, boolean accept) {
		sendChallengeResponse(client.getNumber(), accept);
		
	}
	public void sendChallengeResponse(int clientID, boolean accept) {
		if (accept) {
			synchronized(this) {
				challengeAccepted = true;
			}
		}
		
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_RESPONSE);
		outData.writeInt(clientID);
		outData.writeBoolean(accept);
		serverConnection.sendTcpData(outData);
	}


	

	private void receiveChatMessage(int clientID, String msg) {
		ConnectedClient client = connectedClients.get(clientID);
		chatMessages.add( new ChatMessage(client, msg) );
	}
	private void receiveChallengeRequest(int clientID, String msg) {
		challengeRequests.add(getClient(clientID));
	}
	private void receiveChallengeResponse(int clientID, boolean accept) {
		if (accept) {
			if (!challengeAccepted) { //we have challenged someone, and got a positive response, without having accepted another challenge
				sendChallengeResponse(clientID, true);
			}
			else { //we have already responded positive to someone else, and waiting to receive their response or goto game. Third handshake only reaches server if positive
				sendChallengeResponse(clientID, false);
			}
		}
		else { //not accepted
			if (challengeAccepted) { //we are waiting for response from challengeRequest or third handshake from server
				synchronized( this ) {
					challengeAccepted = false;
				}
			}
			else { //other client declined our challengeRequest
				ConnectedClient client = connectedClients.get(clientID);
				synchronized( client ) {
					client.declineChallenge();
				}
			}
		}
	}
	
	//general
	private synchronized void receiveJoinLobbyResponse(int statusCode) {
		lobbyJoined = true;
	}
	private void receiveClientConnected(int client, String name) {
		addClient(client, name);
	}
	private void receiveClientDisconnected(int client) {
		removeClient(client);
	}
	private void receiveGotoGameRequest(int opponentClientId) {
		
	}
	
	
	private void onTcpData(NetInData data) {
		if (! this.lobbyJoined) return;
		if (this.gotoGame) return;
		
		int messageType = readMessageType(data);
		int clientId;
		
		if (messageType == LobbyNet.CHALLENGE_RESPONSE) {
			clientId = data.readInt();
			boolean accepted = data.readBoolean();
			
			receiveChallengeResponse(clientId, accepted);
		}
		if (this.challengeAccepted) return;
		
		switch(messageType) {
		
		case LobbyNet.CHAT_MESSAGE_ALL:
			clientId = data.readInt();
			String msg = data.readString();
			this.receiveChatMessage(clientId, msg);
			break;
		case LobbyNet.CHALLENGE_REQUEST:
			clientId = data.readInt();
			int challengeType = data.readInt();
			String challengeMessage = data.readString();
			this.receiveChallengeRequest(clientId, challengeMessage);
			break;
		case LobbyNet.CLIENT_CONNECTED:
			clientId = data.readInt();
			String name = data.readString();
			this.receiveClientConnected(clientId, name);
			break;
		case LobbyNet.CLIENT_DISCONNECTED:
			clientId = data.readInt();
			this.receiveClientDisconnected(clientId);
			break;
		case LobbyNet.GOTO_GAME_REQUEST:
			clientId = data.readInt(); //opponent
			this.receiveGotoGameRequest(clientId);
			break;
		case LobbyNet.JOIN_LOBBY_RESPONSE:
			int statusCode = data.readInt();
			this.receiveJoinLobbyResponse(statusCode);
			break;
		default:
			throw new IllegalArgumentException("client got data that it should not get, messageType: "+ messageType);
		}
		
	}
	
	private void writeMessageType(NetOutData data, byte messageType) {
		data.writeByte(messageType);
	}
	private byte readMessageType(NetInData data) {
		return data.readByte();
	}
	private ConnectedClient getClient(int clientID) {
		return connectedClients.get(clientID);
	}
	
	private void addClient(int clientNumber, String name) {
		ConnectedClient client = new ConnectedClient(this, clientNumber, name);
		connectedClients.put(clientNumber, client);
	}
	private void removeClient(ConnectedClient client) {
		removeClient( client.getNumber() );
	}
	private void removeClient(int clientID) {
		connectedClients.remove(clientID);
	}
	

}
