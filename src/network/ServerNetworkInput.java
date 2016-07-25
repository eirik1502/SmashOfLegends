package network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


class ServerNetworkInput extends Thread {

    //ConcurrentHashMap<Socket,ConcurrentLinkedDeque<Task>> taskListHashMap;
	
	private Server server;
    
    private DatagramSocket socket; //UDP
    private DatagramPacket datagramPacket;
    
    private List<ConcurrentLinkedDeque<ClientInput>> clientsInputBuffer = new ArrayList<>();
    private ClientInput[] lastClientsInputOnBuffer = {new ClientInput(), new ClientInput()}; //0,0,0,0
    
    private TimerThread[] clientsTimeoutTimer = new TimerThread[2];
    //private ClientInput lastInput = new ClientInput();
    

    public ServerNetworkInput(Server server, DatagramSocket socket){
    	this.server = server;
    	this.socket = socket;
    }

    @Override
    public void run(){

    	clientsInputBuffer.add( new ConcurrentLinkedDeque<ClientInput>());
    	clientsInputBuffer.add( new ConcurrentLinkedDeque<ClientInput>());
    	
        try {
            //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            //PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        	
        	Host newClient;


            while (true) {

            	byte[] bytes = new byte[Server.RECIEVE_MSG_BYTE_SIZE];
    			datagramPacket = new DatagramPacket(bytes, bytes.length);
    			
    			socket.receive(datagramPacket); //--------------------------------wait for packet
    			//logWriter.println("Recieved packet..");
    			
    			//System.out.println("Server recieved packet");
    			DataInputStream in = new DataInputStream( new ByteArrayInputStream(bytes) );
            	byte msgType = in.readByte();
            	
            	newClient = new Host(datagramPacket);
            	
            	if (msgType == 0) { //join request
            		handleJoinRequest(in, newClient);
            	}
            	else {
            		//apply clientLock, clients cannot be removed
            		if (!server.isClientConnected(newClient)) {
            			
            			System.err.println("Got non-join-request data from random host");
            			continue;
            		}
            		//System.err.println("client is connected");
            		int clientNumber = server.getConnectedClientNumber(newClient);
            		resetTimeoutTimer(clientNumber);
            	
            		//System.out.println("ClientNumber: " + clientNumber);
            		
            		if (msgType == 1) {//ingame data
            			handleGameData(in, clientNumber);
            		}
            		else {
            			throw new IllegalArgumentException("got non-supported data from connected client");
            		}
            		//remove clientLock
            	}
            	
            }
        }
        catch (IOException e){
        	e.printStackTrace();
        	System.out.println("Problems with serviceRequest");
        }
    }
    
    private void handleJoinRequest(DataInputStream in, Host client) throws IOException {
		int receivePort = in.readInt();
		
		client.setReceivePort(receivePort);
		
		int clientNumber = server.connectClient(client);
		boolean requestResponse = false;
		
		if (clientNumber != -1) {
			assignTimeoutTimer(clientNumber);
			requestResponse = true;
		}
		System.out.println("Client trying to join server, was accepted: " + requestResponse + ", client: " + client);
		//logWriter.println("Client trying to join server, was accepted: " + Boolean.toString(requestResponse) + ", client: " + newClient.toString());
		server.sendJoinRequestResponse(client, requestResponse);
    }
    
    private void handleGameData(DataInputStream in, int clientNumber) throws IOException {
		//disconnectedsince last getClientNumb
		//if (!isClientConnected(clientNumber)) break;
		
		float mouseX = in.readFloat();
    	float mouseY = in.readFloat();
    	boolean mvUp = in.readBoolean();
    	boolean mvDown = in.readBoolean();
    	boolean mvLeft = in.readBoolean();
    	boolean mvRight = in.readBoolean();
    	boolean ac1 = in.readBoolean();
    	boolean ac2 = in.readBoolean();
    	
    	ClientInput input = new ClientInput(mouseX, mouseY, mvUp, mvDown, mvLeft, mvRight, ac1, ac2);
    	clientsInputBuffer.get(clientNumber).add(input);

    }
    

    //supposed to give last states when called 60 ups
    public synchronized ClientInput getNextClientInput(int clientNumber) {
    	ConcurrentLinkedDeque<ClientInput> inputBuffer = clientsInputBuffer.get(clientNumber);
//    	if (inputBuffer.isEmpty()) {
//    		return this.lastClientsInputOnBuffer[clientNumber];
//    	}
    	ClientInput nextInput = this.lastClientsInputOnBuffer[clientNumber];
    	while (!inputBuffer.isEmpty()) { //too reach newest input
    		this.lastClientsInputOnBuffer[clientNumber] = inputBuffer.poll();
    	}
    	return nextInput;
    }
    
    public synchronized ClientInput[] getNextClientsInput() {
    	int numberOfClients = 2;
    	ClientInput[] inputs = new ClientInput[numberOfClients];
    	for (int i = 0; i < numberOfClients; i++) {
    		inputs[i] = getNextClientInput(i);
    	}
    	
    	return inputs;
    }
    
    
    
    private void assignTimeoutTimer(int clientNumber) {
    	EmptyActionListener onTimeout = (() -> server.disconnectClientByNumber(clientNumber));
    	TimerThread newTimer = new TimerThread(Server.INACTIVE_TIMEOUT_TIME, onTimeout);
    	clientsTimeoutTimer[clientNumber] = newTimer;
    	clientsTimeoutTimer[clientNumber].start();
    }
    private void resetTimeoutTimer(int clientNumber) {
    	clientsTimeoutTimer[clientNumber].reset();
    }
   
    



}