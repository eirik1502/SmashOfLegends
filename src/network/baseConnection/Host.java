package network.baseConnection;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    private InetAddress address;
    //private int tcpPort, udpPort;
    private int tcpPort, udpPort;
    
//    public Host( InetAddress address, int tcpPort, int udpPort ) {
//    	this.address = address;
//    	this.tcpPort = tcpPort;
//    	this.udpPort = udpPort;
//    }
  public Host( InetAddress address, int tcpPort, int udpPort ) {
	this.address = address;
	this.tcpPort = tcpPort;
	this.udpPort = udpPort;
}
    public Host( DatagramPacket packet ) {
    	this( packet.getAddress(), -1, packet.getPort() );
    }
    public Host( String address,  int tcpPort, int udpPort ) {
		try {
			InetAddress newAddress = InetAddress.getByName(address);
	    	this.address = newAddress;
	    	this.tcpPort = tcpPort;
	    	this.udpPort = udpPort;
	    	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

    }
    
 

	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(String address) {
		try {
			this.address = InetAddress.getByName(address);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void setTcpPort(int port) {
		this.tcpPort = port;
	}
	public int getTcpPort() {
		return tcpPort;
	}
	public void setUdpPort(int port) {
		this.udpPort = port;
	}
	public int getUdpPort() {
		return udpPort;
	}

	
	public boolean equals( Host client ) {
//	    	if (udpPort != -1 && client.getReceivePort() != -1) {
//	    		if (receivePort != client.getReceivePort()) return false;
//	    	}
//	    	if (sendPort != -1 && client.getSendPort() != -1) {
//	    		if (sendPort != client.getSendPort()) return false;
//	    	}
//	    	return (client.getAddress().equals(address) );
    	return (client.getAddress().equals(address) && client.getTcpPort() == tcpPort && client.getUdpPort() == udpPort);
    }
    
    public String toString() {
    	return "{Host, address: " + address.toString() + " tcp port: " + tcpPort + " udp port: " + udpPort + "}";
    }
}
