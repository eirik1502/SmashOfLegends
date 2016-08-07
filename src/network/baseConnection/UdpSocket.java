package network.baseConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class UdpSocket extends Thread {
	
	
	private static int MAX_DATAGRAM_SIZE = 256;
	
	private DatagramSocket socket;
	private Host connectedTo;
	
	private NetInDataListener inputListener;
	
	private boolean running = true;
	
	
	public UdpSocket(DatagramSocket socket, Host connectedTo) {
		this.socket = socket;
		this.connectedTo = connectedTo;
	}

	public synchronized void setDataInListener(NetInDataListener listener) {
		this.inputListener = listener;
	}
	
	@Override
	public void run() {
		if (inputListener == null) throw new IllegalStateException("input data listener must be set before starting udp socket");
		seekInput();
	}
	public synchronized void terminate() {
		running = false;
		socket.close();
		
	}
	
	public synchronized void send(NetOutData data) {
		try {
			//byte msgSize = data.getByteSize();
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getByteSize(), connectedTo.getAddress(), connectedTo.getUdpPort());
			socket.send(packet);
		}
		catch (IOException e) {
			System.err.println("problem with sending data packet in udp socket");
			e.printStackTrace();
		}
	}
	
	private void onInput(byte[] bytes) {
		inputListener.onNetDataReceived(new NetInData(bytes));
	}
	
	private void seekInput() {
		byte[] bytes;
		DatagramPacket receivePacket;

		try {
			
			while(running) {
				
				bytes = new byte[MAX_DATAGRAM_SIZE];
				receivePacket = new DatagramPacket(bytes, bytes.length);
				socket.receive( receivePacket ); //stop and wait
				//i think receivePacket.etData just returns 'bytes', not actual bytes
				bytes = Arrays.copyOfRange(bytes, 0, receivePacket.getLength());
				//System.out.println("udp read byte size: " + bytes.length);

				onInput(bytes);
			}
		
		}
		catch (SocketException e) {
			//if socket is closed while thread is waiting fr read. Pass to finally
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
		finally {
			System.out.println("UdpSocket has closed");
			if (socket != null && !socket.isClosed()) socket.close();
		}
		
	}
	

}
