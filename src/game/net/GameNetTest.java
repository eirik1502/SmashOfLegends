package game.net;

import clientGame.net.ClientGameNet;
import network.baseConnection.Host;
import network.baseConnection.JoinRequestHandeler;
import network.baseConnection.SingleHostNet;
import network.server.ClientInput;
import serverGame.net.ServerGameNet;

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
		
		
		terminate();
		
		boolean result = true;
		System.out.println("\n---------------");
		
		System.out.print("testSendClientInput: ");
		if (testClientInput) {
			System.out.println("true");
		}
		else {
			System.err.println("false");
			result = false;
		}
		
		return result;
	}
	
	public boolean testSendClientInput() {
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
			
			return result;
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException("Got interrupted");
		}
	}
	
	
	
	public boolean setup() {
		joinHandeler = new JoinRequestHandeler(serverPort);
		joinHandeler.setListener(net -> onClientJoin(net));
		joinHandeler.start();
		
		clientNet1 = SingleHostNet.createServerConnection(serverHost);
		clientNet2 = SingleHostNet.createServerConnection(serverHost);
		
		if (serverNet1 == null || serverNet2 == null) System.err.println("Clients could not connect to server");
		
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
		if (clientNet1 != null) clientNet1.terminate();
		if (clientNet2 != null) clientNet2.terminate();
		if (serverNet1 != null) serverNet1.terminate();
		if (serverNet2 != null) serverNet2.terminate();
		if (joinHandeler != null) joinHandeler.terminate();
	}
}
