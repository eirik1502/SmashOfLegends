package clientGame.net;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

import game.net.GameNet;
import game.net.SecureUdpNet;
import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

public class ClientGameNet {

	
	//private SingleHostNet connection;
	private int bufferedInputCount = 0;
	
	private SecureUdpNet secureUdp;
	
	private ConcurrentLinkedDeque<NetInData> inDataGameBuffer = new ConcurrentLinkedDeque<>();
	private ClientGameState lastState = new ClientGameState();
	
	
	
	public ClientGameNet(SingleHostNet connection) {
		//this.connection = connection;
		secureUdp = new SecureUdpNet(data -> onUdpData(data), connection, GameNet.protocolId);
	}
	
	public void setBufferedInputCount(int size) {
		this.bufferedInputCount = size;
	}
	
	public ClientGameState pollGameState() {
		int bufferCount = inDataGameBuffer.size();
		if (bufferCount == 0) {
			return this.lastState;
		}
		
		int pollExtraCount = bufferCount-bufferedInputCount-1;
		
		for (int i = 0; i < pollExtraCount; i++) {
			inDataGameBuffer.poll(); //poll to keep buffer at max bufferdInputCount
		}
		NetInData newData = inDataGameBuffer.poll();
		
		//messageType byte is removed

		ClientGameState newGameState = dataToGameState(newData);
		lastState = newGameState;
		return newGameState;
	}
	private ClientGameState dataToGameState(NetInData in) {

		float camX = in.readFloat();
		float camY = in.readFloat();
		float p1X = in.readFloat();
    	float p1Y = in.readFloat();
    	float p1Direction = in.readFloat();
    	float p1Speed = in.readFloat();
    	float p2X = in.readFloat();
    	float p2Y = in.readFloat();
    	float p2Direction = in.readFloat();
    	float p2Speed = in.readFloat();
    	byte bulletsCreatedCount = in.readByte();
    	NetCameraState cameraState = new NetCameraState(camX, camY);
    	CharacterState player1State = new CharacterState(p1X, p1Y, p1Direction, p1Speed);
    	CharacterState player2State = new CharacterState(p2X, p2Y, p2Direction, p2Speed);
    	
    	ArrayList<NetBulletState> bulletsState = new ArrayList<>();
    	for (int i = 0; i < bulletsCreatedCount; i++) {
    		byte bulletType = in.readByte();
    		float bX = in.readFloat();
        	float bY = in.readFloat();
        	float bDirection = in.readFloat();
        	float bSpeed = in.readFloat();
        	
        	NetBulletState bulletState = new NetBulletState(bulletType, bX, bY, bDirection, bSpeed);
        	bulletsState.add(bulletState);
    	}
    	
    	return new ClientGameState(cameraState, player1State, player2State, bulletsState);
	}
	
	
	private void onUdpData(NetInData data) { //from secureUdpNet
		if (data.readByte() != 1) {
			System.err.println("Client got non-gameState data");
			return;//if data is not game state
		}
		inDataGameBuffer.add(data);
	}
	
	public void sendInputState(boolean keyLeft, boolean keyRight, boolean keyUp, boolean keyDown,
							boolean keyAction1, boolean keyAction2, boolean keyAction3, boolean keyAction4,
							float mouseX, float mouseY) {
		
		NetOutData out = new NetOutData();
		out.writeByte((byte)1); //1 - input state
		
		out.writeBoolean(keyLeft);
		out.writeBoolean(keyRight);
		out.writeBoolean(keyUp);
		out.writeBoolean(keyDown);
		out.writeBoolean(keyAction1);
		out.writeBoolean(keyAction2);
		//out.writeBoolean(keyAction3);
		//out.writeBoolean(keyAction4);
		out.writeFloat(mouseX);
		out.writeFloat(mouseY);
		secureUdp.sendData(out);
	}
	
	
	
}
