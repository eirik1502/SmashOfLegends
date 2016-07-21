package network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    private InetAddress address;
    private int receivePort, sendPort;
    
    public Host( InetAddress address, int receivePort, int sendPort ) {
    	this.address = address;
    	this.receivePort = receivePort;
    	this.sendPort = sendPort;
    }
    public Host( DatagramPacket packet ) {
    	this( packet.getAddress(), -1, packet.getPort() );
    }
    public Host( String address, int receivePort, int sendPort) {
		try {
			InetAddress newAddress = InetAddress.getByName(address);
	    	this.address = newAddress;
	    	this.receivePort = receivePort;
	    	this.sendPort = sendPort;
	    	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

    }
    
    public boolean equals( Host client ) {
    	if (receivePort != -1 && client.getReceivePort() != -1) {
    		if (receivePort != client.getReceivePort()) return false;
    	}
    	if (sendPort != -1 && client.getSendPort() != -1) {
    		if (sendPort != client.getSendPort()) return false;
    	}
    	return (client.getAddress().equals(address) );
    }
    
    public String toString() {
    	return "{Host, address: " + address.toString() + " receivePort: " + Integer.toString(receivePort) + " sendPort: " + Integer.toString(sendPort)+ "}";
    }

	public InetAddress getAddress() {
		return address;
	}
	
	public void setReceivePort(int port) {
		this.receivePort = port;
	}
	public void setSendPort(int port) {
		this.sendPort = port;
	}
	public int getReceivePort() {
		return receivePort;
	}
	public int getSendPort() {
		return sendPort;
	}
    
}
