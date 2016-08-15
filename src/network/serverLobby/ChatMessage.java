package network.serverLobby;

public class ChatMessage {

	
	private int senderId;
	private String message;
	
	
	public ChatMessage(int senderId, String message) {
		this.senderId = senderId;
		this.message = message;
	}


	public int getSenderId() {
		return senderId;
	}
	public String getMessage() {
		return message;
	}
	
	
}
