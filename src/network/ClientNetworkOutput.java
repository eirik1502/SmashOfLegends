package network;


import userInput.InputHandeler;
import userInput.InputState;
import utils.LogWriter;

import static org.lwjgl.glfw.GLFW.*;

import java.io.*;
import java.net.*;
import java.util.Arrays;

 
public class ClientNetworkOutput implements Runnable{

	
	//public static final Host serverHost = new Host("127.0.0.1", Server.PORT_NUMBER);//"192.168.38.101", Server.PORT_NUMBER);
	public static String logFilepath = "src/network/clientLog.txt";
	
	private final Host serverHost;
	
	private DatagramSocket socket;
	
	private LogWriter log;

    private InputHandeler inputHandeler;
    
    
    public ClientNetworkOutput(DatagramSocket socket, Host serverHost,  InputHandeler inputHandeler, LogWriter log) {
    	this.socket = socket;
    	this.serverHost = serverHost;
    	this.inputHandeler = inputHandeler;
    	this.log = log;
    }
    
    public void run(){
    	setup();
    	loop();

    }
    
    private void setup() {

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
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) { //1 second has past
				timer += 1000;
				log.println(updates + " ups, " + frames + " fps"+ "---------------------------------------");
				updates = 0;
				frames = 0;
			}
		}
    }
    
    private void update() {
        //Send new message

    	InputState inputState = inputHandeler.getState();
    	sendInputState(inputState);
    	log.flush();
        
    }
    
    private void sendInputState(InputState input) {
    	
    	//get relevant input
    	float mouseX = input.getMouseX();
    	float mouseY = input.getMouseY();
    	boolean mvUp = input.isKeyboardPressed(GLFW_KEY_W);
    	boolean mvDown = input.isKeyboardPressed(GLFW_KEY_S);
    	boolean mvLeft = input.isKeyboardPressed(GLFW_KEY_A);
    	boolean mvRight = input.isKeyboardPressed(GLFW_KEY_D);
    	boolean ac1 = input.isMousePressed(GLFW_MOUSE_BUTTON_LEFT);
    	boolean ac2 = input.isMousePressed(GLFW_MOUSE_BUTTON_RIGHT);
    	
        try {
        	
        	ByteArrayOutputStream byteStream = new ByteArrayOutputStream(Server.RECIEVE_MSG_BYTE_SIZE);
        	DataOutputStream out = new DataOutputStream( byteStream );
        	
	        out.writeByte(1);//type
	        out.writeFloat(mouseX); //mouse_x
	        out.writeFloat(mouseY); //mouse_y
	        out.writeBoolean(mvUp);
	        out.writeBoolean(mvDown);
	        out.writeBoolean(mvLeft);
			out.writeBoolean(mvRight);
	        out.writeBoolean(ac1);
	        out.writeBoolean(ac2);
	        
	        log.println("Bytes sending: " + out.size() );
	        byte[] bytes = byteStream.toByteArray();
	        log.print("Bytes to send: " + Arrays.toString(bytes) +"..");
	        DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, serverHost.getAddress(), serverHost.getPort());
	        socket.send(datagram);
	        log.println("SUCSESS");
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /*
    private boolean connectToServer(Host serverHost, DatagramSocket socket) {
    	try {
	    	byte messageType = 0;
	    	byte[] bytes = new byte[1];
	    	bytes[0] = messageType;
	    	DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, serverHost.getAddress(), serverHost.getPort());
			socket.send(datagram);
			System.out.println("Join request sendt");
			
			bytes = new byte[Server.SEND_MSG_BYTE_SIZE];
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
    */

}
