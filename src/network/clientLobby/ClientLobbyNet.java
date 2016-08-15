package network.clientLobby;

import java.util.concurrent.ConcurrentLinkedDeque;

import network.LobbyNet;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

public class ClientLobbyNet {


	private SingleHostNet serverConnection;
	private ClientLobbyNetInput inputListener;
	private NetOutData outData;
	
	private ConcurrentLinkedDeque<NetInData> bufferedInData = new ConcurrentLinkedDeque<>();
	
	
	public ClientLobbyNet(SingleHostNet serverConnection, ClientLobbyNetInput inputListener) {
		this.serverConnection = serverConnection;
		this.inputListener = inputListener;
		serverConnection.setTcpDataInListener((data) -> onTcpData(data));
		serverConnection.setUdpDataInListener((data)-> {throw new IllegalArgumentException("Should not receive udp data in lobby");});
		outData = new NetOutData();
	}
	
	/**
	 * retrieves in data and calls respective methods in above layer
	 */
	public void pollInData() {
		while( !this.bufferedInData.isEmpty() ) {
			handleInData(bufferedInData.poll());
		}
	}
	
	public SingleHostNet getConnection() {
		return this.serverConnection;
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
	public void sendChallengeRequest(int clientId) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_REQUEST);
		outData.writeInt(clientId);
		outData.writeInt(0);
		outData.writeString("");
		serverConnection.sendTcpData(outData);
	}
	
	//type - tagetClient(int) - accepted(boolean)

	public void sendChallengeResponse(int clientID, boolean accept) {
		outData.clear();
		writeMessageType(outData, LobbyNet.CHALLENGE_RESPONSE);
		outData.writeInt(clientID);
		outData.writeBoolean(accept);
		serverConnection.sendTcpData(outData);
	}
	
	
	private void onTcpData(NetInData data) {
		this.bufferedInData.add(data);
	}
	
	private void handleInData(NetInData data) {
		int messageType = readMessageType(data);
		int senderId;
		
		switch(messageType) {
		case LobbyNet.JOIN_LOBBY_RESPONSE:
			int statusCode = data.readInt();
			inputListener.receiveJoinLobbyResponse(statusCode);
			break;
		case LobbyNet.CHALLENGE_RESPONSE:
			senderId = data.readInt();
			boolean accept = data.readBoolean();
			inputListener.receiveChallengeResponse(senderId, accept);
			break;
		case LobbyNet.CHAT_MESSAGE_ALL:
			senderId = data.readInt();
			String msg = data.readString();
			inputListener.receiveChatMessage(senderId, msg);
			break;
		case LobbyNet.CHALLENGE_REQUEST:
			senderId = data.readInt();
			int challengeType = data.readInt();
			String challengeMessage = data.readString();
			inputListener.receiveChallengeRequest(senderId, challengeMessage);
			break;
		case LobbyNet.CLIENT_CONNECTED:
			senderId = data.readInt();
			String name = data.readString();
			inputListener.receiveClientConnected(senderId, name);
			break;
		case LobbyNet.CLIENT_DISCONNECTED:
			senderId = data.readInt();
			inputListener.receiveClientDisconnected(senderId);
			break;
		case LobbyNet.GOTO_GAME_REQUEST:
			senderId = data.readInt(); //opponent
			inputListener.receiveGotoGameRequest(senderId);
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
	
}
