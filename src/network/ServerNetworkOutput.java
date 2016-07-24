package network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import gameObjects.Bullet;
import rooms.Entity;

public class ServerNetworkOutput {

	
	private Server server;
	
	private DatagramSocket socket;
	
	
	public ServerNetworkOutput(Server server, DatagramSocket socket){
    	this.server = server;
    	this.socket = socket;
	}
	
	
	public void sendObjectsState(ObjectsState states) {
		CharacterState p1s = states.getPlayer1State();
		CharacterState p2s = states.getPlayer2State();
		ArrayList<CharacterState> createdBulletsState = states.getBulletsCreatedState();
		
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(Server.RECIEVE_MSG_BYTE_SIZE);
        	DataOutputStream out = new DataOutputStream( byteStream );
        	out.writeByte(1); //type
        	out.writeFloat(p1s.getX());
        	out.writeFloat(p1s.getY());
        	out.writeFloat(p1s.getDirection());
        	out.writeFloat(p1s.getSpeed()); //always 0
        	out.writeFloat(p2s.getX());
        	out.writeFloat(p2s.getY());
        	out.writeFloat(p2s.getDirection());
        	out.writeFloat(p2s.getSpeed()); //always 0
        	out.writeByte(createdBulletsState.size()); //number of bullets
        	for (CharacterState bu : createdBulletsState) {
        		out.writeFloat(bu.getX());
        		out.writeFloat(bu.getY());
        		out.writeFloat(bu.getDirection());
        		out.writeFloat(bu.getSpeed());
        		//System.out.println("-------------------------------"+bu);
        	}
        	
    		byte[] bytes = byteStream.toByteArray();
    		
	    	for (int i = 0; i < Server.TOTAL_CLIENT_COUNT; i++) {
	    		if (server.isClientNumberOccupied(i)) {
	    			socketSend(bytes, i);
	    		}
	    	}
    		
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    }
	
	
    public void sendJoinRequestResponse( Host client, boolean accept ) {
    	byte[] data = new byte[2];
    	data[0] = 0;//message type
    	data[1] = (byte) (accept? 1 : 0); //request is accepted;
    	socketSend(data, client);
    }
    
    
    private void socketSend( byte[] data, Host client) {
//    	System.out.println(client.getAddress());
//    	System.out.println(client.getReceivePort());
    	try {
    		DatagramPacket packet = new DatagramPacket( data, data.length, client.getAddress(), client.getReceivePort() );
			socket.send(packet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    private void socketSend( byte[] data, int clientNumber) {
    	socketSend(data, server.getClientByNumber(clientNumber));
    }
    

}
