package network.serverLobby;

public class ChallengeRequest {

	
	private int senderId;
	private int targetId;
	private int challengeType;
	private String challengeMessage;
	
	public ChallengeRequest(int senderId, int targetId, int challengeType, String challengeMessage) {
		this.senderId = senderId;
		this.targetId = targetId;
		this.challengeType = challengeType;
		this.challengeMessage = challengeMessage;
	}

	public int getSenderId() {
		return senderId;
	}
	public int getTargetId() {
		return targetId;
	}
	public int getChallengeType() {
		return challengeType;
	}
	public String getChallengeMessage() {
		return challengeMessage;
	}
	
}
