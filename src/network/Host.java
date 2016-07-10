package network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    private InetAddress address;
    private int port;
    
    public Host( InetAddress address, int port ) {
    	this.address = address;
    	this.port = port;
    }
    public Host( DatagramPacket packet ) {
    	this( packet.getAddress(), packet.getPort() );
    }
    public Host( String address, int port) {
		try {
			InetAddress newAddress = InetAddress.getByName(address);
	    	this.address = newAddress;
	    	this.port = port;
	    	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

    }
    
    public boolean equals( Host client ) {
    	return (client.getAddress().equals(address) && client.getPort() == port);
    }
    public String toString() {
    	return "{Host, address: " + address.toString() + " port: " + Integer.toString(port) + "}";
    }

	public InetAddress getAddress() {
		return address;
	}
	public int getPort() {
		return port;
	}
    
}
