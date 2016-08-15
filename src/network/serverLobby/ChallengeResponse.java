package network.serverLobby;

public class ChallengeResponse {

	
	private int senderId;
	private int targetId;
	private boolean accept;
	
	
	public ChallengeResponse(int senderId, int targetId, boolean accept) {
		this.senderId = senderId;
		this.targetId = targetId;
		this.accept = accept;
	}


	public int getSenderId() {
		return senderId;
	}
	public int getTargetId() {
		return targetId;
	}
	public boolean isAccept() {
		return accept;
	}
	
	
}
