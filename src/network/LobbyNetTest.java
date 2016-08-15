package network;

import java.util.Collection;

import network.baseConnection.Host;
import network.baseConnection.JoinRequestHandeler;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;
import network.clientLobby.ChatMessageContainer;
import network.clientLobby.ClientLobby;
import network.clientLobby.ConnectedClientImage;
import network.serverLobby.ConnectedClient;
import network.serverLobby.ServerLobby;
import network.serverLobby.ServerLobbyNet;

public class LobbyNetTest {

	
	private int serverPort = 9999;
	private final int singleTestWait = 100;
	private final Host serverHost = new Host("127.0.0.1", serverPort, -1);
	
	private JoinRequestHandeler joinHandeler;
	private ServerLobby server;
	private ClientLobby client1, client2;
	
	
	boolean result = true;
	
	
	public static void main(String[] args) {
		LobbyNetTest test = new LobbyNetTest();
		boolean result = test.test();
		System.out.println("ClientLobbyNetTest, result: " + result);
	}
	
	
	public boolean test() {
		System.out.println("Starting test");
		setupServer();
		setupClients();
		
		if (!joinHandeler.isAlive()) System.err.println("joinHnadeler is not running");
		
		boolean joinLobby = testJoinLobby();
		boolean chatMessage = false;
		if (joinLobby) {
			chatMessage = testChatMessage();
		}
		
		System.out.println("wrapping up");
		tearDown();
		
		System.out.println("\n---------------------------------");
		System.out.println("JoinLobby: " + joinLobby);
		if (!joinLobby) System.out.println("JoinLobby have to be true for the rest to be relevant");
		System.out.println("ChatMessage: " + chatMessage);
		return joinLobby&&chatMessage;
	}
	
	//client - server communication
	private boolean testJoinLobby() {
		System.out.println("testing join lobby");
		result = true;
		String name1 = "Infinity";
		String name2 = "Gold digger";
		
		try {
			client1.joinLobby(name1);
			
			Thread.sleep(100);
			server.update();
			System.out.println("Clients connected to server: " +  server.getJoinedClientsCount() );
			
			client2.joinLobby(name2);
			Thread.sleep(100);
			server.update(); //update to receive input and then send accordingly
			
			Thread.sleep(100);
			client1.update(); //update to receive input
			client2.update(); //-"-
			
			int connectionCount = server.getJoinedClientsCount();
			System.out.println("Clients connected to server: " +  connectionCount);
			if (connectionCount != 2) {
				result = false;
			}
			
			int i = 0;
			Collection<ConnectedClient> connectedClients =  server.getJoinedClients();
			for (ConnectedClient c : connectedClients) {
				System.out.println(c);
				if (i == 0) {
					if (!c.getName().equals(name1)) result = false;
				}
				else if (i == 1) {
					if (!c.getName().equals(name2)) result = false;
				}
				i++;
			}
			
			//check if clients received info about each other
			boolean currResult = true;
			Collection<ConnectedClientImage> clients = client1.getJoinedClients();
			System.out.println("Client1 received join count: " + clients.size());
			if (clients.size() != 1) currResult = false;
			for (ConnectedClientImage c : clients) {
				System.out.println("Client1 received joined: " + c.getName());
				if (!c.getName().equals(name2) ) currResult = false;
			}

			clients = client2.getJoinedClients();
			System.out.println("Client2 received join count: " + clients.size());
			if (clients.size() != 1) currResult = false;
			for (ConnectedClientImage c : clients) {
				System.out.println("Client2 received joined: " + c.getName());
				if (!c.getName().equals(name1) ) currResult = false;
			}
			if (currResult) {
				System.out.println("Clients received the other joined client");
			}
			else {
				System.err.println("Clients did not receive the other joined client");
				result = false;
			}
			Thread.sleep(5);
			
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private boolean testChatMessage() {
		try {
			boolean result = true;
			
			String msg1 = "Heio";
			String msg2 = "wapapapppa";
			
			ConnectedClientImage client1Joined = null;
			ConnectedClientImage client2Joined = null;
			Collection<ConnectedClientImage> clients = client1.getJoinedClients();
			for (ConnectedClientImage c : clients) {
				client1Joined = c;
			}
			clients = client2.getJoinedClients();
			for (ConnectedClientImage c : clients) {
				client2Joined = c;
			}
			
			ChatMessage cm = new ChatMessage(client1Joined, msg1);
			client1.publishChatMessage(cm);
			
			Thread.sleep(100);
			server.update();
			Thread.sleep(100);
			client1.update();
			client2.update();
			
			ChatMessageContainer chatContainer1 = client1.getChatContainer();
			ChatMessageContainer chatContainer2 = client2.getChatContainer();
			ChatMessage m1 = chatContainer1.getChatMessages().get(0);
			ChatMessage m2 = chatContainer2.getChatMessages().get(0);
			int m1SenderId = m1.getOwnerClient().getId();
			int m2SenderId = m2.getOwnerClient().getId();
			System.out.println("Chat message received by client1, from: " + m1SenderId + ", read: " + m1.getString());
			System.out.println("Chat message received by client2, from: " + m2SenderId + ", read: " + m2.getString());
			
			if (!m1.getString().equals(msg1) || m1SenderId != client1Joined.getId()) result = false;
			if (!m2.getString().equals(msg1) || m2SenderId != client1Joined.getId()) result = false;
		}
		catch (InterruptedException e ) {
			e.printStackTrace();
		}
		return result;
	}
	private boolean testManualChallengeResponse() {
		
	}
	private boolean testChatMessageAll() {
		
	}
	
	//server - client communication
	private boolean testbJoinLobby() {
		
	}
	private boolean testaJoinLobby() {
		
	}
	
	private void setupClients() {
		SingleHostNet clientConnection = SingleHostNet.createServerConnection(serverHost);
		clientConnection.setTcpDataInListener(data -> {throw new IllegalStateException("client should not get tcp data before setting new listener");} );
		clientConnection.setUdpDataInListener(data -> {throw new IllegalArgumentException("Should not get udp data in this test");} );
		clientConnection.start();
		
		client1 = new ClientLobby(clientConnection);
		
		clientConnection = SingleHostNet.createServerConnection(serverHost);
		clientConnection.setTcpDataInListener(data -> {throw new IllegalStateException("client should not get tcp data before setting new listener");} );
		clientConnection.setUdpDataInListener(data -> {throw new IllegalArgumentException("Should not get udp data in this test");} );
		clientConnection.start();
		
		client2 = new ClientLobby(clientConnection);
	}
	private void setupServer() {
		server = new ServerLobby();
		server.start();
		System.out.println("Server lobby net started");
		
		joinHandeler = new JoinRequestHandeler(serverPort);
		joinHandeler.setListener((connection) -> serverGotConnection(connection));
		joinHandeler.start();
		
		System.out.println("Server is set up");
	}
	private void serverGotConnection(SingleHostNet connection) {
		System.out.println("Client connected to server");
		connection.setTcpDataInListener(data -> {throw new IllegalArgumentException("Should not get data before assigned in lobby");});
		connection.setUdpDataInListener(data -> {throw new IllegalArgumentException("Should not get udp data in this test");});
		connection.start();

		server.addConnection(connection);
	}
//	private void serverGotTcpData(NetInData data) {
//		byte messageType = data.readByte();
//		System.out.println("Server got message, type: " + messageType);
//		switch (messageType) {
//		case LobbyNet.CHAT_MESSAGE_ALL:
//			String msg = data.readString();
//			System.out.println("Got chat message: " + msg);
//			break;
//		case LobbyNet.CHALLENGE_REQUEST:
//			int clientId = data.readInt();
//			int challengeType = data.readInt();
//			String challengeMessage = data.readString();
//			System.out.println("Challenge requset, to clientID: " + clientId + " challengeType: " + challengeType + " challengeMessage: " + challengeMessage);
//			break;
//		case LobbyNet.GOTO_GAME_RESPONSE:
//			byte status = data.readByte(); //opponent
//			byte load = data.readByte();
//			System.out.println("Goto game response, statusCode: " + status + " load%: " + load);
//			break;
//
//		default:
//			throw new IllegalArgumentException("server got data that it should not get, messageType: "+ messageType);
//		}
//	}
	
	private void tearDown() {
		
		client1.getLobbyNet().getConnection().terminate();
		client2.getLobbyNet().getConnection().terminate();
		server.terminate();
		joinHandeler.terminate();
	}
}
