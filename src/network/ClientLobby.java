package network;

import java.util.ArrayList;

public class ClientLobby {

	
	private ArrayList<ConnectedClient> connectedClients = new ArrayList<>();
	private ChatMessageContainer chatContainer;
	
	
	
	
	
	public synchronized void addChatMessage(ChatMessage msg) {
		chatContainer.addMessage(msg);
	}
}
