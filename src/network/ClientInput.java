package network;

public class ClientInput {

	byte msgType;
	float mouseX;
	float mouseY;
	boolean mvUp;
	boolean mvDown;
	boolean mvLeft;
	boolean mvRight;
	boolean ac1;
	boolean ac2;
	
	
	public ClientInput( byte msgType, float mouseX, float mouseY, boolean mvUp,
						boolean mvDown,	boolean mvLeft,	boolean mvRight, boolean ac1, boolean ac2 ) {
	this.msgType = msgType;
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
		return "["+"msgType:"+msgType+" mouseX:"+mouseX+" mouseY:"+mouseY +" mvUp:"+mvUp+" mvDown:"+mvDown+" mvLeft:"+mvLeft+" mvRight:"+mvRight +" action1:"+ac1+" action2:"+ac2+ "]";
	}
}
