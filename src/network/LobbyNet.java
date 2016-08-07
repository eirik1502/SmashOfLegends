package network;

import java.util.HashMap;

public class LobbyNet {

	public static final byte CHALLENGE_REQUEST = 0,
							CHALLENGE_RESPONSE = 1,
							GOTO_GAME_REQUEST = 2,
							GOTO_GAME_RESPONSE = 3,
							CHAT_MESSAGE_ALL = 4,
							CLIENT_CONNECTED = 5,
							CLIENT_DISCONNECTED = 6,
							JOIN_LOBBY_REQUEST = 7,
							JOIN_LOBBY_RESPONSE = 8;
	
	
}
