package network;

public class CharacterState {

	private float x, y, direction, speed;
	
	public CharacterState() {
		this(0,0,0,0);
	}
	public CharacterState(float x, float y, float direction, float speed) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.speed = speed;
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getDirection() {
		return direction;
	}
	public float getSpeed() {
		return speed;
	}
	
	public String toString() {
		return "[CharacterState, x: "+getX()+" y: "+getY()+" dir: "+getDirection()+" speed: "+getSpeed()+"]";
	}
}
