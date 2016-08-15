package network.serverLobby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import network.LobbyNet;
import network.baseConnection.NetInData;
import network.baseConnection.SingleHostNet;


public class ServerLobby implements ConnectedClientsInListener {

	
	private ConcurrentLinkedDeque<ConnectedClient> clientsConnected = new ConcurrentLinkedDeque<>();
	private HashMap<Integer, ConnectedClient> joinedClients = new HashMap<>();
	
//	private ConcurrentLinkedDeque<ChallengeRequest> challengeRequests = new ConcurrentLinkedDeque<>();
//	private ConcurrentLinkedDeque<ChallengeResponse> challengeResponses = new ConcurrentLinkedDeque<>();
//	private ConcurrentLinkedDeque<ChatMessage> chatMessages = new ConcurrentLinkedDeque<>();
	
	private ArrayList<LobbyChallenge> lobbyChallenges = new ArrayList<>();
	
	private boolean running = true;
	private int nextId = 0;
	
	
	public ServerLobby() {
		
	}
	
	public void start() {
		
	}
	
	private boolean challengeExists(LobbyChallenge challenge) {
		for (LobbyChallenge lc : lobbyChallenges) {
			if ( lc.equals(challenge) ) {
				return true;
			}
		}
		return false;
	}
	private ArrayList<LobbyChallenge> getChallenges(ConnectedClient client) {
		ArrayList<LobbyChallenge> result = new ArrayList<>();
		for (LobbyChallenge lc : lobbyChallenges) {
			if (lc.containes(client)) {
				result.add(lc);
			}
		}
		return result;
	}
	
	private void addChallenge(LobbyChallenge challenge) {
		lobbyChallenges.add(challenge);
	}
	private void removeChallenge(LobbyChallenge challenge) {
		lobbyChallenges.remove(challenge);
	}
	
	private void addJoinedClient(ConnectedClient client) {
		this.joinedClients.put(client.getId(), client);
	}
	private void removeJoinedClient(ConnectedClient client) {
		this.joinedClients.remove(client.getId());
	}
	
	public void addConnection(SingleHostNet newConnection) {
		//add conection, then listen for it to join
		clientsConnected.add(new ConnectedClient(this, newConnection, getNewId()));
	}

	private synchronized int getNewId() {
		return nextId++;
	}
	
	public int getJoinedClientsCount() {
		return this.joinedClients.size();
	}
	public Collection<ConnectedClient> getJoinedClients() {
		return this.joinedClients.values();
	}
	
	
	public void clientJoin(ConnectedClient client) {
		client.sendJoinLobbyResponse(1); //1 - OK
		//send all excisting clients to new client and inform existing clients of new client
		for (ConnectedClient c: joinedClients.values()) {
			client.sendClientConnected(c.getId(), c.getName());
			c.sendClientConnected(client.getId(), client.getName());
		}
		this.addJoinedClient(client);
	}
	public void clientLeave(ConnectedClient client) {
		ArrayList<LobbyChallenge> challenges = this.getChallenges(client);
		for (LobbyChallenge challenge : challenges) {
			if (!challenge.ready()) {
				challenge.getOther(client).sendChallengeResponse(client.getId(), false);
				removeChallenge(challenge);
			}
			else {
				System.err.println("Client left after game is ready");
			}
		}
		removeJoinedClient(client);
	}
	
//	public void requestChallenge(int senderId, int targetId, int challengeType, String challengeMessage) {
//		challengeRequests.add(new ChallengeRequest(senderId, targetId, challengeType, challengeMessage));
//	}
//	public void acceptChallenge(int senderId, int targetId) {
//		challengeResponses.add(new ChallengeResponse(senderId, targetId, true));
//	}
//	public void declineChallenge(int senderId, int targetId) {
//		challengeResponses.add(new ChallengeResponse(senderId, targetId, false));
//	}
//	public void publishChatMessage(int senderId, String msg) {
//		chatMessages.add(new ChatMessage(senderId, msg) );
//	}
	
	
	public void startGame(LobbyChallenge challenge) {
		//remember to remove lobbyChallenge somewhere
		removeChallenge(challenge); //do this first, so the leave methods do not get confused by a "ready" challenge being left
		clientLeave(challenge.getClient1());
		clientLeave(challenge.getClient2());
		
		//start the game
	}
	
	
	private void onClientConnected(ConnectedClient c) {
		//connectedClients.put(c.getId(), c);
	}
	private void onChallengeRequest(ChallengeRequest cr) {
		
	}
//	private void onChallengeResponse(ChallengeResponse cr) {
//		int senderId = cr.getSenderId();
//		int targetId = cr.getTargetId();
//		ConnectedClient targetClient = getClient(targetId);
//		boolean accept = cr.isAccept();
//		
//		//check if the two clients are registered
//		LobbyChallenge matching = getChallenge(senderId, targetId);
//		if (matching != null) { //a challenge request is registered
//			
//			if (matching.singleAccept()){ //one response is registered
//				if (accept) {
//					matching.clientAccept(senderId);
//					//gotoGame
//				}
//				else {//not accept, but other part has accepted, last message
//					lobbyChallenges.remove(matching);
//					targetClient.declineChallenge(senderId);
//				}
//			}
//			else { // first register
//				if (accept) {
//					matching.clientAccept(senderId);
//					targetClient.acceptChallenge(senderId);
//				}
//				else {
//					lobbyChallenges.remove(matching);
//					targetClient.declineChallenge(senderId);
//				}
//			}
//			
//		}
//		else throw new IllegalArgumentException("Should not get a challengeResponse if no request is registered");
//		
//	}
	
	
	
	private LobbyChallenge getChallenge(ConnectedClient c1, ConnectedClient c2) {
		for (LobbyChallenge c : lobbyChallenges) {
			if (c.contains(c1, c2)) {
				return c;
			}
		}
		return null;
	}
	private ConnectedClient getClient(int id) {
		return joinedClients.get(id);
	}
	
	public void terminate() {
		for (ConnectedClient c : joinedClients.values()) {
			c.getConnection().terminate();
		}
	}
	
	public void update() {

		for (ConnectedClient client : this.clientsConnected) { //those waiting to join
			client.pollInData();
		}
		for (ConnectedClient client : joinedClients.values()) {
			client.pollInData();
		}

	}

	@Override
	public void receiveJoinLobbyRequest(ConnectedClient sender, String name) {
		System.out.println("lobby received joinLobbyRequest");
		sender.activate(name);
		clientJoin(sender);	
		this.clientsConnected.remove(sender);
	}

	/**
	 * pass challenge if a challenge between the two clients are not registered
	 */
	@Override
	public void receiveChallengeRequest(ConnectedClient sender, int targetId, int challengeType, String challengeMessage) {
		ConnectedClient target = getClient(targetId);
		LobbyChallenge challenge = new LobbyChallenge(sender, target);
		if (!challengeExists(challenge)) {
			addChallenge(challenge);
			target.sendChallengeRequest(sender.getId(), challengeType, challengeMessage);
		}
		
	}

	@Override
	public void receiveChallengeResponse(ConnectedClient sender, int targetId, boolean accept) {
		//int senderId = sender.getId();
		ConnectedClient target = this.getClient(targetId);
		LobbyChallenge challenge = this.getChallenge(sender, target);
		if (challenge == null) {
			System.err.println("server got challenge response, but a challenge between the two clients wasn't registeres");
			return ;
		}
		if (!accept) {
			if (challenge.ready()) {
				System.err.println("Got challenge decline after to accpets, should not happen");
				return;
			}
			if (challenge.singleAccept()) {
				//the second response is false, decline
				System.out.println("One response received, second is decliend");
			}
			else {
				//no responses received, requester waiting
				System.out.println("first response is decline");
			}
			//if single accept or no accept
			target.sendChallengeResponse(sender.getId(), false);
			this.removeChallenge(challenge);
			return;
		}
		//response is true
		challenge.clientAccept(sender); //sender accepts
		
		if (challenge.ready()) {//got second challengeResponse, start game
			this.startGame(challenge);
			//do not pass response
			return;
		}
		if (challenge.singleAccept()) {//got first response
			target.sendChallengeResponse(sender.getId(), true);
			return;
		}
		System.err.println("Should not be reached in handling of challengeResponse receive, server");
	}

	@Override
	public void receiveChatMessage(ConnectedClient sender, String msg) {
		//pass to everybody
		for (ConnectedClient c : joinedClients.values()) {
			c.sendChatMessage(sender.getId(), msg);
		}
	}

	@Override
	public void receiveGotoGameResponse(ConnectedClient sender, byte status, byte loaded) {
		// TODO Auto-generated method stub
		
	}
	
}
