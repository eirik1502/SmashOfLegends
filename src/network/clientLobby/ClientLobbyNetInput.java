package network.clientLobby;

public interface ClientLobbyNetInput {

	public void receiveChatMessage(int senderId, String msg);
	public void receiveChallengeRequest(int senderId, String msg);
	public void receiveChallengeResponse(int senderId, boolean accept);
	//general
	public void receiveJoinLobbyResponse(int statusCode);
	public void receiveClientConnected(int clientId, String name);
	public void receiveClientDisconnected(int clientId);
	public void receiveGotoGameRequest(int opponentClientId);
}
