package network;

public class ClientInput {

	//public final byte msgType;
	public final float mouseX;
	public final float mouseY;
	public final boolean mvUp;
	public final boolean mvDown;
	public final boolean mvLeft;
	public final boolean mvRight;
	public final boolean ac1;
	public final boolean ac2;
	
	
	public ClientInput() {
		this(0f, 0f, false, false, false, false, false, false);
	}
	public ClientInput( float mouseX, float mouseY, boolean mvUp,
						boolean mvDown,	boolean mvLeft,	boolean mvRight, boolean ac1, boolean ac2 ) {
	//this.msgType = msgType;
	this.mouseX = mouseX;
	this.mouseY = mouseY;
	this.mvUp = mvUp;
	this.mvDown = mvDown;
	this.mvLeft = mvLeft;
	this.mvRight = mvRight;
	this.ac1 = ac1;
	this.ac2 = ac2;
	
	}
	
	public String toString() {
		return "["+"mouseX:"+mouseX + " mouseY:"+mouseY +" mvUp:"+mvUp+" mvDown:"+mvDown+" mvLeft:"+mvLeft+" mvRight:"+mvRight +" action1:"+ac1+" action2:"+ac2+ "]";
	}
}
