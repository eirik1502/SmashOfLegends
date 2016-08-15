package network.serverLobby;

public interface ConnectedClientsInListener {

	
	public void receiveJoinLobbyRequest(ConnectedClient sender, String name);
	public void receiveChallengeRequest(ConnectedClient sender, int targetId, int challengeType, String challengeMessage);
	public void receiveChallengeResponse(ConnectedClient sender, int targetId, boolean accept);
	public void receiveChatMessage(ConnectedClient sender, String msg);
	public void receiveGotoGameResponse(ConnectedClient sender, byte status, byte loaded);
}
