package network;

public class NetBulletState extends CharacterState {

	
	private byte no;
	
	
	public NetBulletState(byte no, float x, float y, float direction, float speed) {
		super(x, y, direction, speed);
		this.no = no;
	}
	
	
	public byte getBulletNumber() {
		return no;
	}
}
