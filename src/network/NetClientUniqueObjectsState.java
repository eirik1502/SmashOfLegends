package network;

public class NetClientUniqueObjectsState {

	
	private NetCameraState camera1State, camera2State;

	
	
	public NetClientUniqueObjectsState(NetCameraState camera1State, NetCameraState camera2State) {
		this.camera1State = camera1State;
		this.camera2State = camera2State;
	}
	
	
	public NetCameraState getCamera1State() {
		return camera1State;
	}
	public NetCameraState getCamera2State() {
		return camera2State;
	}
}
