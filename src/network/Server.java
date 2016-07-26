package network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.json.simple.JSONObject;

import game.Game;
import gameObjects.Bullet;
import graphics.Camera;
import rooms.Entity;

public class Server{

	
    public static int PORT_NUMBER = 7770;
    //private static int PORT_NUMBER_SEND = 7771;
    public static int RECIEVE_MSG_BYTE_SIZE = 15;
    public static int SEND_JOIN_RESPONSE_BYTE_SIZE = 2;
    public static int SEND_GAME_DATA_BYTE_SIZE_MAX = 1+8+16*2+16*4; //msgType+cameraPos+playersPos+possibly4Bullets
    
    public static int TOTAL_CLIENT_COUNT = 2;
    public static int INACTIVE_TIMEOUT_TIME = 1000*5;
    
    public static String logFilepath = "src/network/serverLog.txt";
    
    
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    
    private ServerNetworkInput networkInput;
    private ServerNetworkOutput networkOutput;
    
    private Host[] connectedClients = new Host[2];
    
    private int sendStateCounter = 0;
    private int messagesSendt = 0;

    private Game game;
    
    
    
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.runServer();
    }

    public void runServer() {
    	
    	game = new Game();
    	game.init();
    	game.start();
    	
    	
        try {
        	System.out.println("Starting server...");
        	receiveSocket = new DatagramSocket(PORT_NUMBER);
			sendSocket = new DatagramSocket();
			//socket
			System.out.println("Server ready to recieve...");
			
			
	        networkInput = new ServerNetworkInput(this, receiveSocket);
	        networkOutput = new ServerNetworkOutput(this, sendSocket); //not threaded
	        networkInput.start();

            
    		
            long lastTime = System.nanoTime();
    		double delta = 0.0;
    		double ns = 1000000000.0 / 60.0;
    		long timer = System.currentTimeMillis();
    		int updates = 0;
    		int frames = 0;
    		
            //Make new task
            while (true) {
            	
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
    				System.out.println(updates + " ups, " + frames + " fps, " + messagesSendt + " msgs ---------------------------------------");
    				updates = 0;
    				frames = 0;
    				messagesSendt = 0;
    			}
               
 
            }
        }
        catch (SocketException e) {
        	System.err.println("Could not set up sockets");
        	e.printStackTrace();
        }
    }
    
    
    private void update() { //----- send packets
    	ClientInput[] inputs = networkInput.getNextClientsInput();
	    game.update( inputs );
	    
	    	
    	if (this.sendStateCounter++ == 1) { //2    updates 20 times a second
    	    ServerObjectsState objectsState = convertToObjectsState(game.getCameras(), game.getEntities(), game.pollCreatedBullets());
    	    networkOutput.sendObjectsState(objectsState );
    		messagesSendt++;
    		sendStateCounter = 0;
    	}
    	
    }
    

    private ServerObjectsState convertToObjectsState(Camera[] cameras, Entity[] entities, Bullet[] createdBullets) {
    	
    	Entity player1 = entities[0];
		Entity player2 = entities[1];
    	Camera camera1 = cameras[0];
    	Camera camera2 = cameras[1];
    	
		NetCameraState camera1State = new NetCameraState(camera1.getX(), camera1.getY());
		NetCameraState camera2State = new NetCameraState(camera2.getX(), camera2.getY());
		CharacterState player1State = new CharacterState(player1.getX(), player1.getY(), player1.getRotation(), 0f);
		CharacterState player2State = new CharacterState(player2.getX(), player2.getY(), player2.getRotation(), 0f);
	    ArrayList<NetBulletState> bulletsState = new ArrayList<>();
	    for (Bullet bul : createdBullets) {
	    	bulletsState.add( new NetBulletState(bul.getTypeNumber(), bul.getX(), bul.getY(), bul.getRotation(), bul.getSpeed()) );
	    }
    	
    	return new ServerObjectsState(camera1State, camera2State, player1State, player2State, bulletsState);
    }
    
    /**
     * 
     * @param client
     * @return new client number, -1 if server is full
     */
    public synchronized int connectClient(Host client) {
    	for (int i = 0; i < connectedClients.length; i++) {
    		if (!isClientNumberOccupied(i)) {
    			connectedClients[i] = client;
    			return i;
    		}
    	}
    	return -1;
    }
    public synchronized void disconnectClientByNumber(int clientNumber) {
    	connectedClients[clientNumber] = null;
    	System.out.println("Client disconnected");
    }
    public synchronized boolean isClientNumberOccupied(int clientNumber) {
    	if (connectedClients[clientNumber] == null) return false;
    	return true;
    }
    public synchronized boolean isClientConnected(Host client) {
//    	for (Host c : connectedClients) {
//    		if ( c.equals(client)) return true;
//    	}
    	//System.out.println("check client connected");
    	if (connectedClients[0] != null && connectedClients[0].equals(client)) return true;
    	if (connectedClients[1] != null && connectedClients[1].equals(client)) return true;
    	return false;
    }
    /**
     * @param client
     * @return client number, -1 if client doesn't match a connected client
     */
    public synchronized int getConnectedClientNumber( Host client) {
    	int i = 0;
//    	for (Host c : connectedClients) {
//    		if (c != null && c.equals(client)) return i;
//    		i++;
//    	}
    	if (connectedClients[0] != null && connectedClients[0].equals(client)) return 0;
    	if (connectedClients[1] != null && connectedClients[1].equals(client)) return 1;
    	return -1;
    }
    
    public synchronized Host getClientByNumber(int clientNumber) {
    	return connectedClients[clientNumber];
    }
    
    public synchronized void sendJoinRequestResponse( Host client, boolean accept ) {
    	networkOutput.sendJoinRequestResponse(client, accept);
    }
    

}
