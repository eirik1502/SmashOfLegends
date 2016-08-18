package serverGame.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import clientGame.net.ClientGameState;
import game.net.GameNet;
import game.net.SecureUdpNet;
import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;
import network.TimerThread;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;
import network.server.ClientInput;
import network.server.Server;
import trash.GameState;

public class GameClientNet {

	
	private int bufferedInputCount = 0;
	
	private SecureUdpNet udpSecureNet;
	
	private ConcurrentLinkedDeque<ClientInput> inDataInputBuffer = new ConcurrentLinkedDeque();
	
	private ClientInput lastInput = new ClientInput();
	private TimerThread clientsTimeoutTimer;
	 
	 
	public GameClientNet(SingleHostNet connection) {
		 udpSecureNet = new SecureUdpNet(data -> onSecureUdpData(data), connection, GameNet.protocolId);
	}
	
	public void setBufferedInputCount(int size) {
		this.bufferedInputCount = size;
	}
	
	public ClientInput pollInputState() {
		int bufferCount = inDataInputBuffer.size();
		if (bufferCount == 0) {
			return lastInput;
		}
		
		int removeCount = bufferCount-bufferedInputCount-1;
		removeInputBuffer(removeCount); //poll to keep buffer at max bufferdInputCount
		
		lastInput = pollInputBuffer();
		return lastInput;
	}
	
	private void onSecureUdpData(NetInData data) {
		if (data.readByte() != 1) { //if msgType != 1
			System.err.println("Got non-game data from client ingame");
			return;
		}
		addInputBuffer( dataToClientInput(data) );
	}
	
	private ClientInput dataToClientInput(NetInData data) {
		
    	boolean mvLeft = data.readBoolean();
    	boolean mvRight = data.readBoolean();
    	boolean mvUp = data.readBoolean();
    	boolean mvDown = data.readBoolean();
    	boolean ac1 = data.readBoolean();
    	boolean ac2 = data.readBoolean();
    	//boolean ac3 = data.readBoolean();
    	//boolean ac4 = data.readBoolean();
    	float mouseX = data.readFloat();
    	float mouseY = data.readFloat();
    	
    	return new ClientInput(mouseX, mouseY, mvUp, mvDown, mvLeft, mvRight, ac1, ac2);
	}
	private void addInputBuffer(ClientInput input) {
		inDataInputBuffer.add(input);
	}
	private ClientInput pollInputBuffer() {
		return inDataInputBuffer.poll();
	}
	private void removeInputBuffer(int size) {
		for (int i = 0; i < size; i++) {
			inDataInputBuffer.poll();
		}
	}
	
	private void sendData(NetOutData data) {
		udpSecureNet.sendData(data);
	}
	
	/**
	 * static so we dont have to convert state to bytes more than once
	 */
	public static void sendGameState(GameStateNet state, GameClientNet...clients) {
		if (clients.length > 2) throw new IllegalStateException("Only supports sending data to max two clients in game");
		
		NetOutData[] datas = gameStateToData(state);
		for (int i = 0; i < clients.length; i++) {
			clients[i].sendData(datas[i]);
		}
	}
	
	private static NetOutData[] gameStateToData(GameStateNet state) {
		NetCameraState[] camerasState = {state.getCamera1State(), state.getCamera2State() };
		CharacterState p1s = state.getPlayer1State();
		CharacterState p2s = state.getPlayer2State();
		ArrayList<NetBulletState> createdBulletsState = state.getBulletsCreatedState();
		
		try {
			ByteArrayOutputStream postByteStream = new ByteArrayOutputStream(Server.SEND_GAME_DATA_BYTE_SIZE_MAX-9);
			ByteArrayOutputStream preByteStream = new ByteArrayOutputStream(9);
        	DataOutputStream postOut = new DataOutputStream( postByteStream );
        	DataOutputStream preOut = new DataOutputStream( preByteStream );
        	
        	//postOut, general for both clients, preOut special for the clients + mesg type
        	preOut.writeByte(1); //type
        	
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
        	
    		NetOutData[] resData = {new NetOutData(), new NetOutData()};
	    	for (int i = 0; i < Server.TOTAL_CLIENT_COUNT; i++) {
	    		preOut.writeByte(1); //msgType
	    		preOut.writeFloat(camerasState[i].getX());
	    		preOut.writeFloat(camerasState[i].getY());
	    		preOut.write( postByteStream.toByteArray() );
	    		byte[] bytes = preByteStream.toByteArray();
	    		//System.out.println(bytes[0]);
	    		
	    		resData[i].writeBytes(bytes);
	    		preByteStream.reset();
	    	}
    		return resData;
		}
		catch (IOException e1) {
			e1.printStackTrace();
			throw new IllegalStateException("Got an io exception that is fatal in this situation");
		}
	}
	 
}
