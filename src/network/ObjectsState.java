package network;

import java.io.DataOutputStream;
import java.util.ArrayList;

public class ObjectsState {

	private CharacterState player1State, player2State;
	
	private ArrayList<CharacterState> bulletsCreatedState;
	
	
	public ObjectsState() {
		this(new CharacterState(), new CharacterState(), new ArrayList<CharacterState>());
	}
	public ObjectsState( CharacterState player1State, CharacterState player2State, ArrayList<CharacterState> bulletsCreatedState) {
		this.player1State = player1State;
		this.player2State = player2State;
		this.bulletsCreatedState = bulletsCreatedState;
	}

	public void clearBullets() {
		bulletsCreatedState.clear();
	}

	public CharacterState getPlayer1State() {
		return player1State;
	}
	public CharacterState getPlayer2State() {
		return player2State;
	}
	public ArrayList<CharacterState> getBulletsCreatedState() {
		return bulletsCreatedState;
	}
	
	
}
