package network;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import org.json.simple.JSONObject;

public class Server{

    public static int PORT_NUMBER = 7770;
    public static int RECIEVE_MSG_BYTE_SIZE = 15;
    public static int SEND_JOIN_RESPONSE_BYTE_SIZE = 2;
    public static int SEND_GAME_DATA_BYTE_SIZE_MAX = 1+16*2+16*4;
    
    public static String logFilepath = "src/network/serverLog.txt";
    
    private DatagramSocket socket;
    
    private Host[] connectedClients = new Host[2];
    //private int connectedClientCount = 0;

    private Thread clientInputThread;

    
    private int sendStateCounter = 0;
    
    private ConcurrentLinkedDeque<ClientInput> client1InputBuffer = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<ClientInput> client2InputBuffer = new ConcurrentLinkedDeque<>();

    
    private PrintWriter logWriter;
    
    private float mouseX = 0;
    private float mouseY = 0;
    
    
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.runServer();
    }

    public void runServer() {
        try {

        	logWriter = new PrintWriter(logFilepath);
        	
            try {
            	System.out.println("Starting server...");
				socket = new DatagramSocket(PORT_NUMBER);
				System.out.println("Server ready to recieve...");
            
			} catch (SocketException e) {
				e.printStackTrace();
			}
            
        	clientInputThread = new Thread(new ClientInputHandeler(socket));
        	clientInputThread.start();
    		
            long lastTime = System.nanoTime();
    		double delta = 0.0;
    		double ns = 1000000000.0 / 60.0;
    		long timer = System.currentTimeMillis();
    		int updates = 0;
    		int frames = 0;
    		
            //Make new task
            while (true) {

            	/*
                ConcurrentLinkedDeque taskList = taskListHashMap.get(this.clientSocket);
                Task task = (Task) taskList.peek();
                if(task != null){
                    out.println(task.getMessage());
                    taskList.removeFirst();
                    taskListHashMap.put(this.clientSocket,taskList);
                }
                */
            	
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
    				System.out.println(updates + " ups, " + frames + " fps" + "---------------------------------------");
    				updates = 0;
    				frames = 0;
    			}
               
 
            }
        }
        catch (FileNotFoundException e){
        	e.printStackTrace();
        	System.exit(-1);
        }
    }
    
    
    
    private void update() {
    	if (connectedClients[0] != null)
    	{
	    	if (!client1InputBuffer.isEmpty()) {
	    		ClientInput currentInput = client1InputBuffer.poll();
	    		mouseX = currentInput.mouseX;
	    		mouseY = currentInput.mouseY;
	    		//System.out.println("Packet handeled: " + currentInput);
	    	}
	    	else {
	    		System.out.println("Packet loss!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	}
	    	System.out.println("Input client 1 buffer size:" + client1InputBuffer.size());
	    	
	    	
	    	if (this.sendStateCounter++ == 2) { //updates 20 times a second
	    		try {
	    			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(Server.RECIEVE_MSG_BYTE_SIZE);
	            	DataOutputStream out = new DataOutputStream( byteStream );
	            	out.writeByte(1); //type
	            	out.writeFloat(mouseX);
	            	out.writeFloat(mouseY);
	            	out.writeFloat(0);
	            	out.writeFloat(0);
	            	out.writeFloat(0);
	            	out.writeFloat(0);
	            	out.writeFloat(0);
	            	out.writeFloat(0);
	            	out.writeByte(0); //no bullets
		    		byte[] bytes = byteStream.toByteArray();
		    		
		    		Host reciever = this.connectedClients[0];
		    		
		    		DatagramPacket datagram = new DatagramPacket(bytes, bytes.length, reciever.getAddress(), reciever.getPort());
	    		
					socket.send(datagram);
				}
	    		catch (IOException e) {
					e.printStackTrace();
				}
	    		
	    		sendStateCounter = 0;
	    	}
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

                while (true) {

                	byte[] bytes = new byte[RECIEVE_MSG_BYTE_SIZE];
        			datagramPacket = new DatagramPacket(bytes, bytes.length);
        			
        			socket.receive(datagramPacket); //wait for packet
        			logWriter.println("Recieved packet..");
        			
        			System.out.println("Server recieved pacet");
        			DataInputStream in = new DataInputStream( new ByteArrayInputStream(bytes) );
                	byte msgType = in.readByte();
                	
                	Host newClient = new Host(datagramPacket);
                	
                	
                	switch(msgType) {
                	case 0: //join request
                		logWriter.println("..join request");
                		boolean requestResponse = false;
                		
                		if (connectedClients[1] == null) {
                			int clientNumber = 1;
                			if (connectedClients[0] == null ) {
                				connectedClients[0] = newClient;
                				clientNumber = 0;
                			}
                			else
                				connectedClients[1] = newClient;
                			
                			requestResponse = true;
                		}
                		System.out.println("Client trying to join server, was accepted: " + Boolean.toString(requestResponse) + ", client: " + newClient.toString());
                		logWriter.println("Client trying to join server, was accepted: " + Boolean.toString(requestResponse) + ", client: " + newClient.toString());
                		sendJoinRequestResponse(newClient, requestResponse);

                		break;
                		
                	case 1: //game data
                		logWriter.println("..game data");
                		logWriter.println("New client  : " + newClient);
                    	logWriter.println("Comparing to: " + connectedClients[0]);
                    	logWriter.flush();
                		
                		int clientNumber = this.getConnectedClientNumber(newClient);
                		if (clientNumber == -1) {
                			System.err.println("Client not connected trying to send game data (type 1)");
                			logWriter.println("Client not connected trying to send game data (type 1)");
                			break; //not a connected client sending data
                		}
                		
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
                    	logWriter.println("Data: " + input);
                    	
                		break;
                		
                	default:
                		break;
                	}
                	
                	
                	logWriter.flush();

                	//System.out.println("Got message:");
                	//System.out.println("["+ msgType + " " + mouseX + " " + mouseY +" "+mvUp+" "+mvDown+" "+mvLeft+" "+mvRight +" "+ac1+" "+ac2+ "]");
                    
                	//System.out.println("ClientInputBuffer length: " + clientInputBuffer.size());
                	
                	/*String inputLine = in.readLine();
                    Task t = new Task(inputLine, this.clientSocket);
                    addTask(t);*/
                }
            }catch (Exception e){
            	e.printStackTrace();
            	System.out.println("Problems with serviceRequest");
            }
        }
        
        private int getConnectedClientNumber( Host client) {
        	if (connectedClients[0].equals(client)) return 0;
        	if (connectedClients[1].equals(client)) return 1;
        	else return -1;
        }
        
        private void sendJoinRequestResponse( Host client, boolean accept ) {
        	byte[] data = new byte[2];
        	data[0] = 0;
        	data[1] = (byte) (accept? 1 : 0); //request is accepted;
        	socketSend(data, client);
        }
        
        private void socketSend( byte[] data, Host client) {
        	try {
        		DatagramPacket packet = new DatagramPacket( data, data.length, client.getAddress(), client.getPort() );
				socket.send(packet);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }


    }
}
