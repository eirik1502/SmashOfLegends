package network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import gameObjects.Enemy;
import graphics.GraphicsHandeler;
import graphics.Sprite;
import userInput.InputHandeler;
import utils.LogWriter;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
 
public class Client {

	
	public static final Host serverHost = new Host("192.168.38.101", Server.PORT_NUMBER);//"192.168.38.101", Server.PORT_NUMBER);
	
	public static String logFilepath = "src/network/clientLog.txt";
	
	private LogWriter log;
	
	private DatagramSocket socket;
	
	private ClientNetworkOutput networkOutput;
	private ClientNetworkInput networkInput;
	private Thread networkOutputThread;
	private Thread networkInputThread;
    

	private GraphicsHandeler graphicsHandeler;
    private long window;
    private Unit unit;
    
    
    public void start(){
        
    	log = new LogWriter(logFilepath);
    	InputHandeler inputHandeler = setupOther(); //must be called before setting up network
    	setupNetwork(inputHandeler);
    	
    	loop();

    }
    
    private void setupNetwork(InputHandeler inputHandeler) {
    	try {
    		
    		log.print("About to create DatagramSocket.. ");
			socket = new DatagramSocket();
			log.println("SUCSESS");
			
			
			log.print("About to setup connection to server: " + serverHost.toString() +".. ");
			log.flush();
	        boolean connected = false;
	        int tries = 0;
	        while (!connected) {
	        	connected = connectToServer(serverHost, socket);
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
	        networkOutput = new ClientNetworkOutput(socket, serverHost, inputHandeler, log);
	        networkOutputThread = new Thread(networkOutput);
	        networkOutputThread.start();
	        log.printlnSucsess("network output thread");
	        
	        log.println("Setting up network input thread..");
	        networkInput = new ClientNetworkInput(socket, log);
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
    
    private InputHandeler setupOther() {
    	graphicsHandeler = new GraphicsHandeler();
    	window = graphicsHandeler.init();
 
    	Sprite sprite = new Sprite("res/frank_original_rifle.png", 32, 32);
    	unit = new Unit(sprite, 300, 300);
    	
    	graphicsHandeler.addRenderable(unit);
    	
		return new InputHandeler(window);
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
    	float newX = player1State.getX();
    	float newY = player1State.getY();
    	unit.setX(newX);
    	unit.setY(newY);
    }
    private void render() {
    	graphicsHandeler.render();
    }
    
    
    private boolean connectToServer(Host serverHost, DatagramSocket socket) {
    	try {
	    	byte messageType = 0;
	    	byte[] bytes = new byte[1];
	    	bytes[0] = messageType;
	    	DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, serverHost.getAddress(), serverHost.getPort());
			socket.send(datagram);
			System.out.println("Join request sendt");
			
			bytes = new byte[Server.SEND_JOIN_RESPONSE_BYTE_SIZE];
			datagram = new DatagramPacket(bytes, bytes.length);
			
			socket.setSoTimeout(1000); //wait for 1 second
			socket.receive(datagram);
			
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
				socket.setSoTimeout(0);
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