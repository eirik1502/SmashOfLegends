package clientGame.net;

import network.baseConnection.NetOutData;

public class NetSendtData {

	
	private NetOutData data;
	private boolean isAcked = false;
	
	public NetSendtData(NetOutData data) {
		this.data = data;
	}

	public NetOutData getData() {
		return data;
	}

	public boolean isAcked() {
		return isAcked;
	}

	public void setAcked() {
		this.isAcked = true;
	}
	
	
	
}
