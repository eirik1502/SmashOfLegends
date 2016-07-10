package network;

import org.json.simple.JSONObject;

public class NetworkConnection {

private JSONObject json;
	
	
	public NetworkConnection() {
		json = new JSONObject();
	}
	
	
	public boolean connectToServer() {
		return false;
	}
	
	
	public void close() {
		
	}
	
	public void addString(String name, String value) {
		json.put(name, value);
	}
	public void addInt(String name, int value) {
		json.put(name, value);
	}
	public void addFloat(String name, float value) {
		json.put(name, value);
	}
	public void addBoolean(String name, boolean value) {
		json.put(name, value);
	}
	
	public void send() {
		//send
		
		clearJSON();
	}
	
	public void clearJSON() {
		json.clear();
	}
}
