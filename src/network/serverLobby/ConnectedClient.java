package network.serverLobby;

import network.LobbyNet;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

import java.util.concurrent.ConcurrentLinkedDeque;

import javax.sound.midi.SysexMessage;

import org.json.simple.*;

public class ConnectedClient {

	
	private SingleHostNet connection;
	private ServerLobby lobbyNet;
	private ConnectedClientsInListener inListener;
	private int clientId;
	private String name;
	
	private NetOutData outData;
	
	private boolean active;
	//private boolean challengeAccepted = false;
	
	private ConcurrentLinkedDeque<NetInData> bufferedInData = new ConcurrentLinkedDeque<>();
	
	
	public ConnectedClient(ConnectedClientsInListener inListener, SingleHostNet connection, int clientId) {
		//this.lobbyNet = lobbyNet;
		this.inListener = inListener;
		this.connection = connection;
		this.clientId = clientId;
		outData = new NetOutData();
		active = false;
		
		connection.setTcpDataInListener(data -> onTcpData(data));
		connection.setUdpDataInListener(data -> System.err.println("got udp data in serverLobby client"));
	}
//	public ConnectedClient(ServerLobbyNet lobbyNet, SingleHostNet connection,  int clientId, String name) {
//		this.lobbyNet = lobbyNet;
//		this.connection = connection;
//		this.clientId = clientId;
//		this.name = name;
//		
//		outData = new NetOutData();
//		active = true;
//	}
	
	public void pollInData() {
		while(!bufferedInData.isEmpty()) {
			handleInData( bufferedInData.poll() );
		}
	}
	
	private void handleInData(NetInData data) {
		byte messageType = readMessageType(data);
		
		int targetId;
		switch (messageType) {
		case LobbyNet.JOIN_LOBBY_REQUEST:
			String name = data.readString();
			inListener.receiveJoinLobbyRequest(this, name);
			break;
		case LobbyNet.CHALLENGE_REQUEST:
			targetId = data.readInt();
			int challengeType = data.readInt();
			String challengeMessage = data.readString();
			inListener.receiveChallengeRequest(this, targetId, challengeType, challengeMessage);
			break;
		case LobbyNet.CHALLENGE_RESPONSE:
			targetId = data.readInt();
			boolean accept = data.readBoolean();
			inListener.receiveChallengeResponse(this, targetId, accept);
			break;
		case LobbyNet.CHAT_MESSAGE_ALL:
			String msg = data.readString();
			inListener.receiveChatMessage(this, msg);
			break;
		default:
			System.err.println("Got a message of unsupported type, type: " + messageType);	
		}
	}
	
	public void activate(String name) {
		this.name = name;
		active = true;
	}
	
	private void onTcpData( NetInData data) {
		bufferedInData.add(data);
	}
	
	
//	public void joinResponse(int statusCode) {
//		this.sendJoinLobbyResponse(statusCode);
//	}
//	public void clientJoined(int client, String name) {
//		this.sendClientConnected(client, name);
//	}
//	public void clientLeft(int client) {
//		this.sendClientDisconnected(client);
//	}
//	public void requestChallenge(int senderId, int challengeType, String challengeMessage) {
//		sendChallengeRequest(senderId, challengeType, challengeMessage);
//	}
//	public void declineChallenge(int senderId) {
//		sendChallengeResponse(senderId , false);
//	}
//	public void acceptChallenge(int senderId) {
//		sendChallengeResponse(senderId , true);
//	}
//	public void publishChatMessage( int senderId, String message) {
//		sendChatMessage(senderId, message);
//	}
	
	public void sendJoinLobbyResponse(int statusCode) {
		outData.clear();
		writeMessageType(outData, LobbyNet.JOIN_LOBBY_RESPONSE);
		outData.writeInt(statusCode);
		sendData(outData);
		
	}
	public void sendChallengeRequest(int senderId, int challengeType, String challengeMessage) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_REQUEST);
		outData.writeInt(senderId);
		outData.writeInt(challengeType);
		outData.writeString(challengeMessage);
		sendData(outData);
	}
	public void sendChallengeResponse(int senderId, boolean accept) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_RESPONSE);
		outData.writeInt(senderId);
		outData.writeBoolean(accept);
		sendData(outData);
	}
	public void sendClientConnected(int clientId, String name) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CLIENT_CONNECTED);
		outData.writeInt(clientId);
		outData.writeString(name);
		sendData(outData);
	}
	public void sendClientDisconnected(int clientId) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CLIENT_DISCONNECTED);
		outData.writeInt(clientId);
		sendData(outData);
	}
	public void sendChatMessage(int senderId, String msg) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHAT_MESSAGE_ALL);
		outData.writeInt(senderId);
		outData.writeString(msg);
		sendData(outData);
	}
	public void sendGotoGameReqeust(int opponentId) {
		outData.clear();
		writeMessageType(outData, LobbyNet.GOTO_GAME_REQUEST);
		outData.writeInt(opponentId);
		sendData(outData);
	}
	
	private void sendData(NetOutData data) {
		connection.sendTcpData(data);
	}
	
	
//	private void receiveJoinLobbyRequest(String name) {
//		if (active) System.err.println("Got join request, already connected");
//		else {
//			activate(name);
//			//this.sendJoinLobbyResponse(1);
//			lobbyNet.clientJoined(this);
//		}
//	}
//	private void receiveChallengeRequest(int targetId, int challengeType, String challengeMessage) {
//		lobbyNet.requestChallenge(getId(), targetId, challengeType, challengeMessage);
//	}
//	private void receiveChallengeResponse(int targetId, boolean accept) {
//		if (accept) lobbyNet.acceptChallenge(getId(), targetId);
//		else lobbyNet.declineChallenge(getId(), targetId);
//	}
//	private void receiveChatMessage(String msg) {
//		lobbyNet.publishChatMessage(getId(), msg);
//	}
//	private void receiveGotoGameResponse(byte status, byte loaded) {
//		
//	}
	
	
	
	public int getId() {
		return clientId;
	}
	public String getName() {
		return name;
	}
	public SingleHostNet getConnection() {
		return this.connection;
	}
	
	
	private void writeMessageType(NetOutData data, byte messageType) {
		data.writeByte(messageType);
	}
	private byte readMessageType(NetInData data) {
		return data.readByte();
	}

	
	public String toString() {
		return "[ConnectedClient; id: "+getId()+", name: "+getName()+", active: "+active+"]";
	}
}
