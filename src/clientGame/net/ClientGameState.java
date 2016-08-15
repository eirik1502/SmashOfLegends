package clientGame.net;


import java.io.DataOutputStream;
import java.util.ArrayList;

import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;

public class ClientGameState {

	
	private NetCameraState cameraState;
	
	private CharacterState player1State, player2State;
	
	
	private ArrayList<NetBulletState> bulletsCreatedState;
	
	
	public ClientGameState() {
		this(new NetCameraState(), new CharacterState(), new CharacterState(), new ArrayList<NetBulletState>());
	}
	public ClientGameState( NetCameraState cameraState, CharacterState player1State, CharacterState player2State, ArrayList<NetBulletState> bulletsCreatedState) {
		this.cameraState = cameraState;
		this.player1State = player1State;
		this.player2State = player2State;
		this.bulletsCreatedState = bulletsCreatedState;
	}

	public void clearBullets() {
		bulletsCreatedState.clear();
	}
	
	
	public NetCameraState getCameraState() {
		return this.cameraState;
	}
	public CharacterState getPlayer1State() {
		return player1State;
	}
	public CharacterState getPlayer2State() {
		return player2State;
	}
	public ArrayList<NetBulletState> getBulletsCreatedState() {
		return bulletsCreatedState;
	}
	
	@Override
	public String toString() {
		return "["+cameraState+player1State+player2State+bulletsCreatedState+"]";
	}
}
