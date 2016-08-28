package serverGame.net;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import game.net.CharacterState;
import game.net.GameNet;
import game.net.NetBulletState;
import game.net.NetCameraState;
import game.net.SecureUdpNet;
import network.TimerThread;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;
import network.server.ClientInput;

public class ServerGameNet {

	
	private GameClientNet[] gameClients = new GameClientNet[2];
	
	
	
	public ServerGameNet(SingleHostNet clientNet1, SingleHostNet clientNet2) {
		gameClients[0] = new GameClientNet(clientNet1);
		gameClients[1] = new GameClientNet(clientNet2);
		gameClients[0].setBufferedInputCount(0);
		gameClients[1].setBufferedInputCount(0);
	}
	
	public ClientInput[] pollClientsInput() {
		ClientInput[] res = {gameClients[0].pollInputState(), gameClients[1].pollInputState() };
		return res;
	}
	
	public void sendGameState(GameStateNet gameState) {
		GameClientNet.sendGameState(gameState, gameClients);
	}
	
}

