package serverGame.net;


import java.io.DataOutputStream;
import java.util.ArrayList;

import network.CharacterState;
import network.NetBulletState;
import network.NetCameraState;

public class GameStateNet {

	
	private NetCameraState camera1State, camera2State;
	private CharacterState player1State, player2State;
	
	private ArrayList<NetBulletState> bulletsCreatedState;
	
	
	public GameStateNet() {
		this(new NetCameraState(), new NetCameraState(), new CharacterState(), new CharacterState(), new ArrayList<NetBulletState>());
	}
	public GameStateNet( NetCameraState camera1State, NetCameraState camera2State, CharacterState player1State, CharacterState player2State, ArrayList<NetBulletState> bulletsCreatedState) {
		this.camera1State = camera1State;
		this.camera2State = camera2State;
		this.player1State = player1State;
		this.player2State = player2State;
		this.bulletsCreatedState = bulletsCreatedState;
	}

	public void clearBullets() {
		bulletsCreatedState.clear();
	}
	
	
	
	public NetCameraState getCamera1State() {
		return camera1State;
	}
	public NetCameraState getCamera2State() {
		return camera2State;
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
	
	
}
