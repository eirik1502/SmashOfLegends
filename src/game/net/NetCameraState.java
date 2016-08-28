package game.net;

public class NetCameraState {

	
	private float x, y;
	
	
	public NetCameraState() {
		this(0, 0);
	}
	
	public NetCameraState(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	
	@Override
	public String toString() {
		return "[CameraState x: "+x+", y: "+y+"]";
	}
}
