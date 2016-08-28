package clientGame;



import network.baseConnection.Host;
import network.server.Server;
import server.ServerMain;

public class ConnectionRoom extends NetRoom{
	

	private final Host serverHost = new Host("", ServerMain.SERVER_CONNECT_PORT, -1);
	
	
	public ConnectionRoom() {
		super(ClientMain.WINDOW_WIDTH, ClientMain.WINDOW_HEIGHT);
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {

		if (super.isServerConnection()) {
			System.out.println("Connection alreadyEstablished");
		}
		else {
			super.addEntity( new IpUserInput(o -> onIpUserInput(o)) );
			System.out.println("Creating ip input object");
		}
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}
	
	
	private void onIpUserInput(IpUserInput o) {
		System.out.println("Client trying to connect to: " + o.getResult());
		if (serverHost.setAddress(o.getResult())) { // false if address is not valid
			System.out.println("Server host connecting to: " + serverHost.toString());
			if (super.createServerConnection(serverHost)) { //false if connection could not be made
				super.removeEntity(o);
				this.gotoNextRoom();
				return;
			}	
		}
		o.reset("woops");
		//o.reset("Could not connect to given address ):");

	}

}
