package network.tests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import network.baseConnection.NetInData;
import network.baseConnection.NetInDataListener;
import network.baseConnection.NetOutData;
import network.baseConnection.TcpSocket;

public class TcpSocketTest {

	
	private Socket socket;
	
	private Server serverThread;
	
	private TcpSocket tcpSocket;
	
	private boolean result = false;
	
	private final byte testByte = 5;
	
	

	public static void main(String[] a) {
		TcpSocketTest test = new TcpSocketTest();
		System.out.println("TcpSocketTest success: " + test.test());
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
			tcpSocket.send(outData);
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
			socket = new Socket("127.0.0.1", 9998);
			System.out.println("socket is connected");
			NetInDataListener listener = (inData) -> onServerData(inData);
			tcpSocket = new TcpSocket(socket);
			tcpSocket.setDataInListener(listener);
			tcpSocket.start();
			
		}
		catch (IOException e) {
			System.err.println("Could not connect socket");
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		serverThread.terminate();
		tcpSocket.terminate();
		try {
			serverThread.join();
			tcpSocket.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public class Server extends Thread{
		private ServerSocket socket;
		private TcpSocket tcpSocket;
		
		@Override
		public void run() {
			try {
				socket = new ServerSocket(9998);
				
				System.out.println("server port up on 9998");
				Socket clientSocket = socket.accept();
				System.out.println("Server listening");
				
				
				NetInDataListener listener = data -> {
					byte inByte = data.readByte();
					System.out.println("got client data: "+data +" byte: "+inByte);
					if (inByte == 5) {
						NetOutData odata = new NetOutData();
						odata.writeByte((byte)(inByte*2));
						tcpSocket.send(odata);
						System.out.println("server replied double");
					}
				};
				tcpSocket = new TcpSocket(clientSocket);
				tcpSocket.setDataInListener(listener);
				tcpSocket.start();
				
				NetOutData data = new NetOutData();
				data.writeByte((byte)1);
				data.writeFloat(10f);
				data.writeFloat(11f);
				data.writeFloat(12f);
				tcpSocket.send(data);
				System.out.println("Server sendt data");
				
				
				
			}
			catch (IOException e) {
				System.err.println("Could not connect socket");
				e.printStackTrace();
			}
			
		}
		
		public synchronized void terminate() {
			try {
				socket.close(); //close the server socket, separat from tcpSocket->socket
				tcpSocket.terminate();
				tcpSocket.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
