package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkUtils {

	private NetworkUtils() {
		
	}
	
	public static void writeCharacterState(CharacterState characterState, DataOutputStream out) {
		try {
			out.writeFloat(characterState.getX());
			out.writeFloat(characterState.getY());
			out.writeFloat(characterState.getDirection());
			out.writeFloat(characterState.getSpeed());
		}
		catch (IOException e) {
			System.err.println("IO exception occured while writeing character state");
			e.printStackTrace();
		}
	}
	public static void writeObjectsState(ObjectsState objectsState, DataOutputStream out) {
		try {
			writeCharacterState(objectsState.getPlayer1State(), out); //write player 1
			writeCharacterState(objectsState.getPlayer1State(), out); //write player 2
			ArrayList<CharacterState> bulletsState = objectsState.getBulletsCreatedState();
			out.writeByte(bulletsState.size()); //write number of bullets to come
			for (CharacterState bulletState : bulletsState) { //write bullets
				writeCharacterState(bulletState, out);
			}
		}
		catch (IOException e) {
			System.err.println("IO exception occured while writeing objects state");
			e.printStackTrace();
		}
	}
}
