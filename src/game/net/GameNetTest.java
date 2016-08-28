package game.net;

import java.util.ArrayList;

import clientGame.net.ClientGameNet;
import clientGame.net.ClientGameState;
import network.baseConnection.Host;
import network.baseConnection.JoinRequestHandeler;
import network.baseConnection.SingleHostNet;
import network.baseConnection.TcpSocket;
import network.baseConnection.UdpSocket;
import network.server.ClientInput;
import serverGame.net.GameStateNet;
import serverGame.net.ServerGameNet;
import trash.GameState;

public class GameNetTest {

	private int serverPort = 9999;
	private Host serverHost = new Host("127.0.0.1", serverPort, -1);
	
	private SingleHostNet clientNet1, clientNet2;
	private ClientGameNet clientGame1, clientGame2;
	
	private JoinRequestHandeler joinHandeler;
	private ServerGameNet serverGame;
	private SingleHostNet serverNet1, serverNet2;
	
	
	public static void main(String[] args) {
		GameNetTest test = new GameNetTest();
		System.out.println("GameNetTest: " + test.test());
	}
	
	

	public boolean test() {
		if (!setup()){
			System.err.println("Could not setup test");
			return false;
		}
		boolean testClientInput = testSendClientInput();
		boolean testGameState = testSendGameState();
		
		terminate();
		
		boolean result = true;
		System.out.println("\n-----RESULT-----");
		
		System.out.print("testSendClientInput: ");
		if (testClientInput) {
			System.out.println("true");
		}
		else {
			System.err.println("false");
			result = false;
		}
		
		System.out.print("testSendGameState: ");
		if (testGameState) {
			System.out.println("true");
		}
		else {
			System.err.println("false");
			result = false;
		}
		
		return result;
	}
	
	
	public boolean testSendGameState() {
		System.out.println("\n----- testing send game state-----");
		try {
			//send game state
			CharacterState c1State = new CharacterState(250, 1000, 4f, 0);
			CharacterState c2State = new CharacterState(500, 400, 2.5f, 0);
			NetCameraState cam1 = new NetCameraState(250, 1000);
			NetCameraState cam2 = new NetCameraState(500, 400);
			ArrayList<NetBulletState> bulletsState = new ArrayList<>();
			bulletsState.add(new NetBulletState((byte)1, 300f, 600f, 0f, 10f));
			GameStateNet gameState = new GameStateNet(cam1, cam2, c1State, c2State, bulletsState);
			serverGame.sendGameState(gameState);
			
			Thread.sleep(100);
			
			//read game state
			ClientGameState gameState1 = clientGame1.pollGameState();
			ClientGameState gameState2 = clientGame2.pollGameState();
			
			boolean result = true;
			
			if (gameState1.getCameraState().getX() != cam1.getX()) {
				System.err.println("Cam1 was incorrect");
				result = false;
			}
			if (gameState2.getCameraState().getX() != cam2.getX()){
				System.err.println("Cam2 was incorrect");
				result = false;
			}
			if (! gameState1.getPlayer1State().equals(c1State)) {
				System.err.println("Player1 state is incorrect at player1");
				result = false;
			}
			if (! gameState2.getPlayer1State().equals(c1State)) {
				System.err.println("Player1 state is incorrect at player2");
				result = false;
			}
			if (! gameState1.getPlayer2State().equals(c2State)) {
				System.err.println("Player2 state is incorrect at player1");
				result = false;
			}
			if (! gameState2.getPlayer2State().equals(c2State)) {
				System.err.println("Player2 state is incorrect at player2");
				result = false;
			}
			
			if (gameState1.getBulletsCreatedState().size() != bulletsState.size()) {
				System.err.println("Did not get 1 created bullet at player 1");
				result = false;
			}
			if (gameState2.getBulletsCreatedState().size() != bulletsState.size()) {
				System.err.println("Did not get 1 created bullet at player 2");
				result = false;
			}
			
			return result;
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return false;
	}
	public boolean testSendClientInput() {
		System.out.println("\n----- testing send client input-----");
		try {
			clientGame1.sendInputState(true, false, false, false, true, false, false, false, 300f, 300f);
			clientGame2.sendInputState(false, true, false, false, false, true, false, false, 500f, 500f);
			
			Thread.sleep(100);
			
			ClientInput[] inputs = serverGame.pollClientsInput();
			ClientInput input1 = inputs[0];
			ClientInput input2 = inputs[1];
			
			System.out.println("\nReceived input from clients:");
			System.out.println("Client1: " + input1);
			System.out.println("Client2: " + input2);
			
			boolean result = true;
			if (!input1.mvLeft){
				System.err.println("Client1 mvLeft failed");
				result = false;
			}
			if (input1.mvRight){
				System.err.println("Client1 mvRight failed");
				result = false;
			}
			if (input2.mvLeft){
				System.err.println("Client2 mvLeft failed");
				result = false;
			}
			if (!input2.mvRight){
				System.err.println("Client2 mvRight failed");
				result = false;
			}
			
			Thread.sleep(100); //just to calm down
			
			return result;
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException("Got interrupted");
		}
	}
	
	
	
	public boolean setup() {
		System.out.println("\n----- seting up-----");
		joinHandeler = new JoinRequestHandeler(serverPort);
		joinHandeler.setListener(net -> onClientJoin(net));
		joinHandeler.start();
		
		//serverHost is not changed
		clientNet1 = SingleHostNet.createServerConnection(serverHost);
		clientNet2 = SingleHostNet.createServerConnection(serverHost);
		UdpSocket cSockU1 = clientNet1.getUdpSocket();
		UdpSocket cSockU2 = clientNet2.getUdpSocket();
		TcpSocket cSockT1 = clientNet1.getTcpSocket();
		TcpSocket cSockT2 = clientNet2.getTcpSocket();
		
		System.out.println("Client1 udp port target: " + cSockU1.getPort() );
		System.out.println("Client2 udp port target: " + cSockU2.getPort() );
		System.out.println("Clients UdpSockets are equal: " + (cSockU1 == cSockU2) );
		System.out.println("Clients UdpTargetPorts are equal: " + (cSockU1.getPort() == cSockU2.getPort()) );
		System.out.println("Clients UdpLocalPorts are equal: " + (cSockU1.getLocalPort() == cSockU2.getLocalPort()) );
		System.out.println("Clients tcp local ports; 1: " + cSockT1.getLocalPort() + ", 2: " + cSockT2.getLocalPort());
		
		if (serverNet1 == null || serverNet2 == null) System.err.println("Clients could not connect to server");
		if (serverNet1 == serverNet2) System.err.println("the two connections on server side are equal");
		if (clientNet1 == clientNet2) System.err.println("The two client connections are equal");
		
		
		clientNet1.setTcpDataInListener(data -> System.err.println("Should not get data yet"));
		clientNet1.setUdpDataInListener(data -> System.err.println("Should not get data yet"));
		clientNet2.setTcpDataInListener(data -> System.err.println("Should not get data yet"));
		clientNet2.setUdpDataInListener(data -> System.err.println("Should not get data yet"));
		serverNet1.setTcpDataInListener(data -> System.err.println("Should not get data yet"));
		serverNet1.setUdpDataInListener(data -> System.err.println("Should not get data yet"));
		serverNet2.setTcpDataInListener(data -> System.err.println("Should not get data yet"));
		serverNet2.setUdpDataInListener(data -> System.err.println("Should not get data yet"));
		
		clientNet1.start();
		clientNet2.start();
		serverNet1.start();
		serverNet2.start();
		
		clientGame1 = new ClientGameNet(clientNet1);
		clientGame2 = new ClientGameNet(clientNet2);
		serverGame = new ServerGameNet(serverNet1, serverNet2);

		return true;
	}
	private void onClientJoin(SingleHostNet net) {
		System.out.println("Server got connection");
		synchronized(this) {
			if (serverNet1 == null) {
				serverNet1 = net;
			}
			else if (serverNet2 == null) {
				serverNet2 = net;
			}
			else {
				System.err.println("More than two clients tried to join");
			}
		}
	}
	
	private void terminate() {
		System.out.println("\n-----terminating-----");
		if (clientNet1 != null) clientNet1.terminate();
		if (clientNet2 != null) clientNet2.terminate();
		if (serverNet1 != null) serverNet1.terminate();
		if (serverNet2 != null) serverNet2.terminate();
		if (joinHandeler != null) joinHandeler.terminate();
	}
}
