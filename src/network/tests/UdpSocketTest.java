package network.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import network.baseConnection.Host;
import network.baseConnection.NetInData;
import network.baseConnection.NetInDataListener;
import network.baseConnection.NetOutData;
import network.baseConnection.UdpSocket;

public class UdpSocketTest {

	
	private DatagramSocket socket;
	
	private Server serverThread;
	
	private UdpSocket udpSocket;
	
	private boolean result = false;
	
	private final byte testByte = 5;
	
	

	public static void main(String[] a) {
		UdpSocketTest test = new UdpSocketTest();
		System.out.println("UdpSocketTest success: " + test.test() );
	}
	
	
	private void setup() {
		
		serverThread = new Server();
	}
	
	private synchronized void onServerData(NetInData inData) {
		byte inByte = inData.readByte();
		if (inByte == 1) {
			System.out.println("client got data");
			NetOutData outData = new NetOutData();
			outData.writeByte(testByte);
			udpSocket.send(outData);
		}
		else if (inByte == testByte*2) {
			result = true;
		}
		else {
			System.err.println("got server data, byte != 1");
		}
		
	}
	
	public boolean test() {
		setup();
		
		serverThread.start();
		
		try {
			Host server = new Host("127.0.0.1", -1, 9998);
			socket = new DatagramSocket();
			System.out.println("socket is created");
			NetInDataListener listener = (inData) -> onServerData(inData);
			udpSocket = new UdpSocket(socket, server);
			udpSocket.setDataInListener(listener);
			udpSocket.start();
			
			NetOutData data = new NetOutData();
			data.writeByte((byte)0);
			udpSocket.send(data);
			//join request
			
		}
		catch (IOException e) {
			System.err.println("Could not connect socket");
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		serverThread.terminate();
		udpSocket.terminate();
		try {
			serverThread.join();
			udpSocket.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		while(running) {
//			
//		}
		
//		try {
//			socket.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return result;
	}
	
	public class Server extends Thread{
		private DatagramSocket socket;
		private UdpSocket udpSocket;
		
		private boolean result;
		
		@Override
		public void run() {
			try {
				socket = new DatagramSocket(9998);
				
				System.out.println("server port up on 9998");
				byte[] bytes = new byte[1];
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				socket.receive(packet); //wait for connection
				Host client = new Host(packet.getAddress(), -1, packet.getPort());
				System.out.println("Client connected,  listening");
				
				
				NetInDataListener listener = data -> {
					byte inByte = data.readByte();
					System.out.println("got client data: "+data +" byte: "+inByte);
					if (inByte == 5) {
						NetOutData odata = new NetOutData();
						odata.writeByte((byte)(inByte*2));
						udpSocket.send(odata);
						System.out.println("server replied double");
					}

				};
				udpSocket = new UdpSocket(socket, client);
				udpSocket.setDataInListener(listener);
				udpSocket.start();
				
				NetOutData data = new NetOutData();
				data.writeByte((byte)1);
				data.writeFloat(10f);
				data.writeFloat(11f);
				data.writeFloat(12f);
				udpSocket.send(data);
				System.out.println("Server sendt data");
				
				
				
			}
			catch (IOException e) {
				System.err.println("Could not connect socket");
				e.printStackTrace();
			}
			
			
		}
		
		public synchronized void terminate() {
			udpSocket.terminate();
			try {
				udpSocket.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
