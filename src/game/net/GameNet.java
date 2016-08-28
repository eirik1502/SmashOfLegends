package game.net;

public class GameNet {

    public static int SEND_GAME_DATA_BYTE_SIZE_MAX = 1+8+16*2+16*4; //msgType+cameraPos+playersPos+possibly4Bullets

    
	public static final int protocolId = 0x130f0c01; //"sol1" by character place in alphabet
	

}
