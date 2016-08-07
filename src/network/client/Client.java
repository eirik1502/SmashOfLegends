package network.client;


import game.Game;
import gameObjects.Enemy;
import gameObjects.Equinox;
import graphics.Camera;
import graphics.Font;
import graphics.GraphicsEntity;
import graphics.GraphicsHandeler;
import graphics.GraphicsUtils;
import graphics.Sprite;
import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;
import network.ClientGameObjects.ClientBackground;
import network.ClientGameObjects.ClientBulletEntity;
import network.ClientGameObjects.ClientCharacterEntity;
import network.ClientGameObjects.ClientEntity;
import network.ClientGameObjects.ClientEquinox;
import network.baseConnection.Host;
import network.server.Server;
import physics.Collideable;
import physics.PhysicsHandeler;
import rooms.Entity;
import rooms.Text;
import userInput.InputHandeler;
import utils.LogWriter;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
 
public class Client {

	

	public static final Host serverHost = new Host("", -1, Server.PORT_NUMBER);//"192.168.38.102", Server.PORT_NUMBER);
	
	public static String logFilepath = "src/network/clientLog.txt";
	
	
	//private LogWriter log;
	
	private DatagramSocket receiveSocket, sendSocket;
	
	private ClientNetworkOutput networkOutput;
	private ClientNetworkInput networkInput;
	private Thread networkOutputThread;
	private Thread networkInputThread;
    

	//private GraphicsHandeler graphicsHandeler;
    //private long window;
	private Camera camera;
	
    private ClientCharacterEntity[] characters = new ClientCharacterEntity[2];
    private ArrayList<ClientBulletEntity> bullets = new ArrayList<>();
    private ArrayList<ClientEntity> entities = new ArrayList<>();
	private LinkedList<ClientEntity> addEntityBuffer = new LinkedList<>();
	private LinkedList<ClientEntity> removeEntityBuffer = new LinkedList<>();
	protected boolean updatingEntities = false;
    
    private float inputStateSendInterval = 60.0f;
    
    
    //game stuff
    private long window;
    private InputHandeler inputHandeler;
    private GraphicsHandeler graphicsHandeler;
    
   
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }
   
    
    
    public void start(){
        
    	//log = new LogWriter(logFilepath);
    	setupOther();//call this first
    	setupNetwork();
    	
    	loop();
    }
    
    private void connectToServer() {
    }
    
    private void setupNetwork() {
    	try {
    		
    		//log.print("About to create DatagramSocket.. ");
			receiveSocket = new DatagramSocket();
			sendSocket = new DatagramSocket();
			//System.out.println("sockets ports: " + receiveSocket.getPort() + ", " + sendSocket.get);
			//log.println("SUCSESS");
			
			
			//log.print("About to setup connection to server: " + serverHost.toString() +".. ");
			//log.flush();
	        boolean connected = false;
	        int tries = 0;
	        while (!connected) {
	        	connected = connectToServer(serverHost, receiveSocket, sendSocket);
	        	if (tries++ == 10) {
	        		//log.errPrintln("\nTried to connect to server " + (tries) + " times, no sucsess:");
	        		//log.errPrintln("-- server might be offline\n-- too many packets lost");
	        		//log.flush();
	        		throw new IllegalStateException("Tried to connect to server " + tries + " times, no sucsess..");
	        	}
	        }
	      //  log.println("SUCSESS after " + (tries) +" connection attempts");
	        //log.flush();
	        
	        //log.println("Setting up network output thread..");
	        networkOutput = new ClientNetworkOutput(sendSocket, serverHost, inputStateSendInterval);
	        networkOutputThread = new Thread(networkOutput);
	        networkOutputThread.start();
	        //log.printlnSucsess("network output thread");
	        
	        //log.println("Setting up network input thread..");
	        networkInput = new ClientNetworkInput(receiveSocket);
	        networkInputThread = new Thread(networkInput);
	        networkInputThread.start();
	        //log.printlnSucsess("network input thread");
		}
    	catch (SocketException e) {
			e.printStackTrace();
			System.err.println("Could not create a socket :(");
			System.exit(-1);
		}
    }
   
    
    private void setupOther() {
    	window = GraphicsUtils.createWindowOpenGl(Game.WIDTH, Game.HEIGHT, "Smash of Legends");
		graphicsHandeler = new GraphicsHandeler(window, Game.WIDTH, Game.HEIGHT);
		inputHandeler = new InputHandeler(window);
		
		//get ip
		IpUserInput ipInput = new IpUserInput(graphicsHandeler);
		String ip = ipInput.getInput();
		if (ip == "") {
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}
		String connectingText = "Trying to connect to server at '" + ip + "', please wait ...";
		Text[] texts = { new Text(connectingText, Font.getStandardFont(), 18, 300f, 150f, 0) };
		graphicsHandeler.renderTexts(texts);
		serverHost.setAddress(ip);
		
 
		ClientCharacterEntity.loadSprite();
		ClientBulletEntity.loadSprites();
		ClientEquinox.loadSprite();
    	characters[0] = new ClientCharacterEntity(100, 100, 0);
    	characters[1] = new ClientCharacterEntity(100, 100, 0);
    	
    	ClientEquinox p1e = new ClientEquinox(this, Equinox.p1StartX, Equinox.p1StartY);
    	ClientEquinox p2e = new ClientEquinox(this, Equinox.p2StartX, Equinox.p2StartY);
    	
    	addEntity(new ClientBackground());
    	addEntity(characters[0]);
    	addEntity(characters[1]);
    	addEntity(p1e);
    	addEntity(p2e);
    	
    	
    	camera = new Camera(Game.WIDTH/2-200, Game.HEIGHT/2, Game.WIDTH, Game.HEIGHT);
    	//graphicsHandeler.addRenderable(unit);

    }
    
    private void update() {
    	glfwPollEvents(); //to allow input on networkOutputThread
    	
    	updatingEntities = true;
    	entities.forEach(b -> b.update());
    	updatingEntities = false;
		while(!addEntityBuffer.isEmpty()) {
			addEntity(addEntityBuffer.poll());
		}
		while(!removeEntityBuffer.isEmpty()) {
			removeEntity(removeEntityBuffer.poll());
		}
        
    	ClientObjectsState objectsState = networkInput.getNextObjectsState();
    	NetCameraState cameraState = objectsState.getCameraState();
    	CharacterState player1State = objectsState.getPlayer1State();
    	CharacterState player2State = objectsState.getPlayer2State();
    
    	camera.setX(cameraState.getX());
    	camera.setY(cameraState.getY());
    	characters[0].setCharacterState(player1State);
    	characters[1].setCharacterState(player2State);

    	for (NetBulletState b : objectsState.getBulletsCreatedState()) {
    		ClientBulletEntity bullet = ClientBulletEntity.getBulletByNumber(this, b);//new ClientBulletEntity(this, b, b.getSpeed());
    		addBullet(bullet);
    		characters[0].shootAnimation();
    		System.out.println("Bullet created: "+bullet);
    	}
    }
    
    
    private void render() {
    	graphicsHandeler.render(camera, new ArrayList<GraphicsEntity>(entities) );
    }
    
    
    public ClientCharacterEntity collideCharacter(Collideable c) {
		for (ClientCharacterEntity character : characters) {
			if (PhysicsHandeler.isCollision(c, character)) {
				return character;
			}
		}
		return null;
    }
    
    public void removeBullet(ClientBulletEntity bullet) {
    	removeEntity(bullet);
    }
    public void addBullet(ClientBulletEntity bullet) {
    	addEntity(bullet);
    }
    
    public void removeEquinox(ClientEquinox e) {
    	this.removeEntity(e);
    }
    
	private void addEntity(ClientEntity e) {
		if (updatingEntities) { //not added at all
			addEntityBuffer.add(e);
			return;
		}
		else {
			entities.add(e);
//			e.roomInit(this);
//			e.start();
		}
	}
	private void removeEntity(ClientEntity e) {
		if (updatingEntities) { //not added at all
			removeEntityBuffer.add(e);
			return;
		}
		else {
			entities.remove(e);
		}
	}
    
    private void loop() {
    	long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while(!glfwWindowShouldClose(window)) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) { //1/60 of a second has past
				
				update();
				
				updates++;
				delta--;
			}
            
			render();
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) { //1 second has past
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps"+ "---------------------------------------");
				updates = 0;
				frames = 0;
			}
		}
		
		networkInput.terminate();
		networkOutput.terminate();
		try {
			networkInputThread.join();
			networkOutputThread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.sendSocket.close();
		this.receiveSocket.close();
		glfwDestroyWindow(window);
		glfwTerminate();
    }
    
    
    private boolean connectToServer(Host serverHost, DatagramSocket receiveSocket, DatagramSocket sendSocket) {
    	try {
	    	byte messageType = 0;
	    	ByteBuffer bbuffer = ByteBuffer.allocate(5);
	    	bbuffer.put(messageType);
	    	bbuffer.putInt(receiveSocket.getLocalPort());
	    	byte[] bytes = bbuffer.array();
	    	int a = 12;
	    	
	    	DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, serverHost.getAddress(), serverHost.getUdpPort());
			sendSocket.send(datagram);
			Host dummyHost = new Host("192.168.38.103", 1000, 1000);
			DatagramPacket dummyDatagram = new DatagramPacket(new byte[0], 0, dummyHost.getAddress(), dummyHost.getUdpPort());
			receiveSocket.send(dummyDatagram); //
			System.out.println("Join request sendt");
			
			bytes = new byte[Server.SEND_JOIN_RESPONSE_BYTE_SIZE];
			datagram = new DatagramPacket(bytes, bytes.length);
			
			
			receiveSocket.setSoTimeout(1000); //wait for 1 second
			receiveSocket.receive(datagram);
			
			if (bytes[0] != 0) throw new IllegalStateException("Got game data before connection was established");
			if (bytes[1] == 0) throw new IllegalStateException("Could not connect to server, server is full");
			
			System.out.println("Connection to server established");
			return true;
		}
    	catch (SocketTimeoutException e) {
    		System.out.println("Could not connect to server, server is offline, or request packet lost");
    		return false;
    	}
    	catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Could not connect to server, unknown reason (IOException), se error message above");
		}
    	finally {
    		try {
				receiveSocket.setSoTimeout(0);
			}
    		catch (SocketException e) {
				e.printStackTrace();
			}
    	}

    }
    

}