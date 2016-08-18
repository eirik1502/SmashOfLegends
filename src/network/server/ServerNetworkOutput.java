package network.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;
import network.baseConnection.Host;
import serverGame.Entity;
import serverGame.entities.Bullet;
import serverGame.net.GameStateNet;

public class ServerNetworkOutput {

	
	private Server server;
	
	private DatagramSocket socket;
	
	
	public ServerNetworkOutput(Server server, DatagramSocket socket){
    	this.server = server;
    	this.socket = socket;
	}
	
	
	public void sendObjectsState(GameStateNet states) {
		NetCameraState[] camerasState = {states.getCamera1State(), states.getCamera2State() };
		CharacterState p1s = states.getPlayer1State();
		CharacterState p2s = states.getPlayer2State();
		ArrayList<NetBulletState> createdBulletsState = states.getBulletsCreatedState();
		
		try {
			ByteArrayOutputStream postByteStream = new ByteArrayOutputStream(Server.SEND_GAME_DATA_BYTE_SIZE_MAX-9);
			ByteArrayOutputStream preByteStream = new ByteArrayOutputStream(9);
        	DataOutputStream postOut = new DataOutputStream( postByteStream );
        	DataOutputStream preOut = new DataOutputStream( preByteStream );
        	
        	//postOut, general for both clients, preOut special for the clients + mesg type
        	//preOut.writeByte(1); //type
        	postOut.writeFloat(p1s.getX());
        	postOut.writeFloat(p1s.getY());
        	postOut.writeFloat(p1s.getDirection());
        	postOut.writeFloat(p1s.getSpeed()); //always 0
        	postOut.writeFloat(p2s.getX());
        	postOut.writeFloat(p2s.getY());
        	postOut.writeFloat(p2s.getDirection());
        	postOut.writeFloat(p2s.getSpeed()); //always 0
        	postOut.writeByte(createdBulletsState.size()); //number of bullets
        	for (NetBulletState bu : createdBulletsState) {
        		postOut.writeByte(bu.getBulletNumber()); //bullet type
        		postOut.writeFloat(bu.getX());
        		postOut.writeFloat(bu.getY());
        		postOut.writeFloat(bu.getDirection());
        		postOut.writeFloat(bu.getSpeed());
        		//System.out.println("-------------------------------"+bu);
        	}
        	
    		
	    	for (int i = 0; i < Server.TOTAL_CLIENT_COUNT; i++) {
	    		preOut.writeByte(1); //msgType
	    		preOut.writeFloat(camerasState[i].getX());
	    		preOut.writeFloat(camerasState[i].getY());
	    		preOut.write( postByteStream.toByteArray() );
	    		byte[] bytes = preByteStream.toByteArray();
	    		//System.out.println(bytes[0]);
	    		
	    		if (server.isClientNumberOccupied(i)) {
	    			socketSend(bytes, i);
	    		}
	    		
	    		preByteStream.reset();
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
    		DatagramPacket packet = new DatagramPacket( data, data.length, client.getAddress(), client.getUdpPort() );
			socket.send(packet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    private void socketSend( byte[] data, int clientNumber) {
    	socketSend(data, server.getClientByNumber(clientNumber));
    }
    

}
