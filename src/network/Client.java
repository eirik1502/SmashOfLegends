package network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import game.Game;
import gameObjects.Enemy;
import graphics.GraphicsHandeler;
import graphics.GraphicsUtils;
import graphics.Sprite;
import rooms.Entity;
import rooms.Text;
import userInput.InputHandeler;
import utils.LogWriter;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
 
public class Client {

	
	public static final Host serverHost = new Host("192.168.38.102", Server.PORT_NUMBER, -1);//"192.168.38.101", Server.PORT_NUMBER);
	
	public static String logFilepath = "src/network/clientLog.txt";
	
	private LogWriter log;
	
	private DatagramSocket receiveSocket, sendSocket;
	
	private ClientNetworkOutput networkOutput;
	private ClientNetworkInput networkInput;
	private Thread networkOutputThread;
	private Thread networkInputThread;
    

	//private GraphicsHandeler graphicsHandeler;
    //private long window;
    private Entity unit;
    
    private float inputStateSendInterval = 60.0f;
    
    
    //game stuff
    private long window;
    private InputHandeler inputHandeler;
    private GraphicsHandeler graphicsHandeler;
    
    
    
    public void start(){
        
    	log = new LogWriter(logFilepath);
    	setupOther();//call this first
    	setupNetwork();
    	
    	loop();
    }
    
    private void connectToServer() {
    }
    
    private void setupNetwork() {
    	try {
    		
    		log.print("About to create DatagramSocket.. ");
			receiveSocket = new DatagramSocket();
			sendSocket = new DatagramSocket();
			//System.out.println("sockets ports: " + receiveSocket.getPort() + ", " + sendSocket.get);
			log.println("SUCSESS");
			
			
			log.print("About to setup connection to server: " + serverHost.toString() +".. ");
			log.flush();
	        boolean connected = false;
	        int tries = 0;
	        while (!connected) {
	        	connected = connectToServer(serverHost, receiveSocket, sendSocket);
	        	if (tries++ == 10) {
	        		log.errPrintln("\nTried to connect to server " + (tries) + " times, no sucsess:");
	        		log.errPrintln("-- server might be offline\n-- too many packets lost");
	        		log.flush();
	        		throw new IllegalStateException("Tried to connect to server " + tries + " times, no sucsess..");
	        	}
	        }
	        log.println("SUCSESS after " + (tries) +" connection attempts");
	        log.flush();
	        
	        log.println("Setting up network output thread..");
	        networkOutput = new ClientNetworkOutput(sendSocket, serverHost, inputStateSendInterval,  log);
	        networkOutputThread = new Thread(networkOutput);
	        networkOutputThread.start();
	        log.printlnSucsess("network output thread");
	        
	        log.println("Setting up network input thread..");
	        networkInput = new ClientNetworkInput(receiveSocket, log);
	        networkInputThread = new Thread(networkInput);
	        networkInputThread.start();
	        log.printlnSucsess("network input thread");
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
 
    	Sprite sprite = new Sprite("res/frank_original_rifle.png", 32, 32);
    	unit = new Unit(sprite, 300, 300);
    	
    	//graphicsHandeler.addRenderable(unit);

    }
    
    private void loop() {
    	long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while(true) {
			
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
    }
    
    
    
    private void update() {
    	glfwPollEvents(); //to allow input on networkOutputThread
        
    	ObjectsState objectsState = networkInput.getNextObjectsState();
    	CharacterState player1State = objectsState.getPlayer1State();
    	
    	unit.setX(player1State.getX());
    	unit.setY(player1State.getY());
    	unit.setRotation(player1State.getDirection());
    }
    
    private void render() {
    	Entity[] e = {unit};
    	graphicsHandeler.render(e, new Text[0]);
    }
    
    
    private boolean connectToServer(Host serverHost, DatagramSocket receiveSocket, DatagramSocket sendSocket) {
    	try {
	    	byte messageType = 0;
	    	ByteBuffer bbuffer = ByteBuffer.allocate(5);
	    	bbuffer.put(messageType);
	    	bbuffer.putInt(receiveSocket.getLocalPort());
	    	byte[] bytes = bbuffer.array();
	    	int a = 12;
	    	
	    	DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, serverHost.getAddress(), serverHost.getReceivePort());
			sendSocket.send(datagram);
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
    


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }
}