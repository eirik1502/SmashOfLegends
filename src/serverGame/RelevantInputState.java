package serverGame;

public class RelevantInputState {

	float mouseX;
	float mouseY;
	boolean mvUp;
	boolean mvDown;
	boolean mvLeft;
	boolean mvRight;
	boolean ac1;
	boolean ac2;
	
	
	public RelevantInputState( float mouseX, float mouseY, boolean mvUp,
						boolean mvDown,	boolean mvLeft,	boolean mvRight, boolean ac1, boolean ac2 ) {
	this.mouseX = mouseX;
	this.mouseY = mouseY;
	this.mvUp = mvUp;
	this.mvDown = mvDown;
	this.mvLeft = mvLeft;
	this.mvRight = mvRight;
	this.ac1 = ac1;
	this.ac2 = ac2;
	
	}
	
	
	
	public float getMouseX() {
		return mouseX;
	}
	public float getMouseY() {
		return mouseY;
	}
	public boolean isMoveUp() {
		return mvUp;
	}
	public boolean isMoveDown() {
		return mvDown;
	}
	public boolean isMoveLeft() {
		return mvLeft;
	}
	public boolean isMoveRight() {
		return mvRight;
	}
	public boolean isAction1() {
		return ac1;
	}
	public boolean isAction2() {
		return ac2;
	}


	public String toString() {
		return "["+" mouseX:"+mouseX+" mouseY:"+mouseY +" mvUp:"+mvUp+" mvDown:"+mvDown+" mvLeft:"+mvLeft+" mvRight:"+mvRight +" action1:"+ac1+" action2:"+ac2+ "]";
	}
}
