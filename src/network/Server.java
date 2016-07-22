package network;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import org.json.simple.JSONObject;

import game.Game;
import gameObjects.Bullet;
import rooms.Entity;

public class Server{

    public static int PORT_NUMBER = 7770;
    //private static int PORT_NUMBER_SEND = 7771;
    public static int RECIEVE_MSG_BYTE_SIZE = 15;
    public static int SEND_JOIN_RESPONSE_BYTE_SIZE = 2;
    public static int SEND_GAME_DATA_BYTE_SIZE_MAX = 1+16*2+16*4;
    
    
    public static String logFilepath = "src/network/serverLog.txt";
    
    
    private DatagramSocket recieveSocket;
    private DatagramSocket sendSocket;
    
    
    private Host[] connectedClients = new Host[2];
	private final int activityTimeout = 1000*5;
	//private int[] clientsActive = {activityTimeout, activityTimeout};
	private TimerThread[] clientsTimeoutTimer = new TimerThread[2];
	//private boolean[] disconnectClientsFlag = {false, false};
	
    //private int connectedClientCount = 0;

	private ClientInputHandeler inputHandeler;
    private Thread clientInputThread;

    
    private int sendStateCounter = 0;
    
    private ConcurrentLinkedDeque<ClientInput> client1InputBuffer = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<ClientInput> client2InputBuffer = new ConcurrentLinkedDeque<>();
    private ClientInput lastInput = new ClientInput();
    
    
    private PrintWriter logWriter;
    
    private float mouseX = 0;
    private float mouseY = 0;
    
    
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

        	logWriter = new PrintWriter(logFilepath);
        	
            try {
            	System.out.println("Starting server...");
				recieveSocket = new DatagramSocket(PORT_NUMBER);
				sendSocket = new DatagramSocket();
				//socket
				System.out.println("Server ready to recieve...");
            
			} catch (SocketException e) {
				e.printStackTrace();
			}
            
            inputHandeler = new ClientInputHandeler(recieveSocket);
        	clientInputThread = new Thread(inputHandeler);
        	clientInputThread.start();
    		
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
        catch (FileNotFoundException e){
        	e.printStackTrace();
        	System.exit(-1);
        }
    }
    
    
    private void update() { //----- send packets
    	ClientInput[] clientsInput = {new ClientInput(), new ClientInput()};
    	for (int i = 0; i < connectedClients.length; i++) {
	    	if (connectedClients[i] != null)
	    	{
	    		ConcurrentLinkedDeque<ClientInput> clientInputBuffer = (i == 0)? client1InputBuffer:client2InputBuffer;
	    		//System.out.println("Input client 1 buffer size:" + client1InputBuffer.size());
	    		if (!clientInputBuffer.isEmpty()) {
	    		}
	    		else {
	    			
	    		}
	    		
	    		while (!clientInputBuffer.isEmpty()) {
		    		lastInput = clientInputBuffer.poll();
		    		//System.out.println("Packet handeled: " + currentInput);
		    	}
	    		clientsInput[i] = lastInput;
	    		
	//	    	else {
	//	    		System.out.println("Packet loss!!!!!!!!!!!!!!!!!!!!!!!!!");
	//	    	}
	    	}
    	}
	    	
	    game.update(clientsInput);
	    	
    	if (this.sendStateCounter++ == 2) { //updates 20 times a second
	    			
    		Entity[] entities = game.getEntities();
    		Bullet[] createdBullets = game.pollCreatedBullets();
    		//the two players will be the first two elements
    		Entity a = entities[0]; //player
    		Entity b = entities[1]; //enemy
    		try {
    			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(Server.RECIEVE_MSG_BYTE_SIZE);
            	DataOutputStream out = new DataOutputStream( byteStream );
            	out.writeByte(1); //type
            	out.writeFloat(a.getX());
            	out.writeFloat(a.getY());
            	out.writeFloat(a.getRotation());
            	out.writeFloat(0);
            	out.writeFloat(b.getX());
            	out.writeFloat(b.getY());
            	out.writeFloat(b.getRotation());
            	out.writeFloat(0);
            	out.writeByte(createdBullets.length); //number of bullets
            	for (Bullet bu : createdBullets) {
            		out.writeFloat(bu.getX());
            		out.writeFloat(bu.getY());
            		out.writeFloat(bu.getRotation());
            		out.writeFloat(bu.getSpeed());
            		System.out.println("-------------------------------"+bu);
            	}
            	
	    		byte[] bytes = byteStream.toByteArray();
	    		
		    	for (int i = 0; i < connectedClients.length; i++) {
		    		if (connectedClients[i] != null) {
		    			Host reciever = this.connectedClients[i];
			    		DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, reciever.getAddress(), reciever.getReceivePort());
						sendSocket.send(datagram);
			    	}
		    	}
	    		
			}
    		catch (IOException e1) {
				e1.printStackTrace();
			}
	    	
	    		
    		messagesSendt++;
    		sendStateCounter = 0;
    	}
    	
    	
    }
    


    class ClientInputHandeler implements Runnable {

        //ConcurrentHashMap<Socket,ConcurrentLinkedDeque<Task>> taskListHashMap;
        
        private DatagramSocket socket; //UDP
        private DatagramPacket datagramPacket;
        

        public ClientInputHandeler(DatagramSocket socket){
        	this.socket = socket;
        }

        public void run(){

            try {
                //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                //PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            	
            	Host newClient;


                while (true) {

                	byte[] bytes = new byte[RECIEVE_MSG_BYTE_SIZE];
        			datagramPacket = new DatagramPacket(bytes, bytes.length);
        			
        			socket.receive(datagramPacket); //--------------------------------wait for packet
        			//logWriter.println("Recieved packet..");
        			
        			System.out.println("Server recieved pacet");
        			DataInputStream in = new DataInputStream( new ByteArrayInputStream(bytes) );
                	byte msgType = in.readByte();
                	
                	newClient = new Host(datagramPacket);
                	
                	
                	switch(msgType) {
                	case 0: //join request
                		logWriter.println("..join request");
                		boolean requestResponse = false;
                		int receivePort = in.readInt();
                		
                		newClient.setReceivePort(receivePort);
                		
                		if (connectedClients[1] == null) {
                			int clientNumber = 1;
                			if (connectedClients[0] == null ) {
                				connectedClients[0] = newClient;
                				clientNumber = 0;
                			}
                			else
                				connectedClients[1] = newClient;
                			
                			assignTimeoutTimer(clientNumber);
                			requestResponse = true;
                		}
                		System.out.println("Client trying to join server, was accepted: " + Boolean.toString(requestResponse) + ", client: " + newClient.toString());
                		logWriter.println("Client trying to join server, was accepted: " + Boolean.toString(requestResponse) + ", client: " + newClient.toString());
                		sendJoinRequestResponse(newClient, requestResponse);

                		break;
                		
                	case 1: //game data
                		//logWriter.println("..game data");
                		//logWriter.println("New client  : " + newClient);
                    	//logWriter.println("Comparing to: " + connectedClients[0]);
                    	//logWriter.flush();
                		
                		int clientNumber = this.getConnectedClientNumber(newClient);
                		if (clientNumber == -1) {
                			System.err.println("Client not connected trying to send game data (type 1)");
                			logWriter.println("Client not connected trying to send game data (type 1)");
                			break; //not a connected client sending data
                		}
                		resetTimeoutTimer(clientNumber);
                		//disconnectedsince last getClientNumb
                		if (!isClientConnected(clientNumber)) break;
                		
                		float mouseX = in.readFloat();
                    	float mouseY = in.readFloat();
                    	boolean mvUp = in.readBoolean();
                    	boolean mvDown = in.readBoolean();
                    	boolean mvLeft = in.readBoolean();
                    	boolean mvRight = in.readBoolean();
                    	boolean ac1 = in.readBoolean();
                    	boolean ac2 = in.readBoolean();
                    	
                    	ClientInput input = new ClientInput( msgType, mouseX, mouseY, mvUp, mvDown, mvLeft, mvRight, ac1, ac2);
                    	if (clientNumber == 0) {
                    		client1InputBuffer.add(input);
                    	}
                    	else if (clientNumber == 1) {
                    		client2InputBuffer.add(input);
                    	}
                    	//logWriter.println("Data: " + input);
                    	
                		break;
                		
                	default:
                		break;
                	}
                	
                	
                	//logWriter.flush();

                	//System.out.println("Got message:");
                	//System.out.println("["+ msgType + " " + mouseX + " " + mouseY +" "+mvUp+" "+mvDown+" "+mvLeft+" "+mvRight +" "+ac1+" "+ac2+ "]");
                    
                	//System.out.println("ClientInputBuffer length: " + clientInputBuffer.size());
                	
                	/*String inputLine = in.readLine();
                    Task t = new Task(inputLine, this.clientSocket);
                    addTask(t);*/
                }
            }
            catch (IOException e){
            	e.printStackTrace();
            	System.out.println("Problems with serviceRequest");
            }
        }
        
        private void assignTimeoutTimer(int clientNumber) {
        	EmptyActionListener onTimeout = (() -> disconnectClient(clientNumber));
        	TimerThread newTimer = new TimerThread(activityTimeout, onTimeout);
        	clientsTimeoutTimer[clientNumber] = newTimer;
        	clientsTimeoutTimer[clientNumber].start();
        }
        private void resetTimeoutTimer(int clientNumber) {
        	clientsTimeoutTimer[clientNumber].reset();
        }
        
        private void disconnectClient(int clientNumber) {
        	connectedClients[clientNumber] = null;
        	System.out.println("Client disconnected");
        }
        
        private boolean isClientConnected(int clientNumber) {
        	if (connectedClients[clientNumber] == null) return false;
        	return true;
        }
        
        private int getConnectedClientNumber( Host client) {
        	if (connectedClients[0] != null && connectedClients[0].equals(client)) return 0;
        	if (connectedClients[1] != null && connectedClients[1].equals(client)) return 1;
        	else return -1;
        }
        
        private void sendJoinRequestResponse( Host client, boolean accept ) {
        	byte[] data = new byte[2];
        	data[0] = 0;//message type
        	data[1] = (byte) (accept? 1 : 0); //request is accepted;
        	socketSend(data, client);
        }
        
        private void socketSend( byte[] data, Host client) {
        	System.out.println(client.getAddress());
        	System.out.println(client.getReceivePort());
        	try {
        		DatagramPacket packet = new DatagramPacket( data, data.length, client.getAddress(), client.getSendPort() );
				socket.send(packet);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }


    }
}
