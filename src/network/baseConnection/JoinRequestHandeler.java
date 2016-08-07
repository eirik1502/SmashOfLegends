package network.baseConnection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class JoinRequestHandeler extends Thread{

	
	private boolean running = true;
	
	private ServerSocket serverSocket;
	private int port;
	
	private JoinRequestListener listener;
	
	
	
	public JoinRequestHandeler(int port) {
		this.port = port;
	}
	
	public synchronized void terminate() {
		running = false;
		try {
			serverSocket.close();
			this.join();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void setListener( JoinRequestListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void run() {
		if (listener == null) throw new IllegalStateException("set a listener beore starting join request handeler");
		
		try {
			serverSocket = new ServerSocket(port);
			
			while(running) {
				
				onJoinRequest( serverSocket.accept() ); //wait for request
				
			}
		}
		catch (SocketException e) {
			//if socket is closed on terminate
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onJoinRequest(Socket socket) {
		System.out.println("Server created new socket on port: " + socket.getLocalPort());
		try {
			TcpSocket tcpSocket = new TcpSocket(socket);
			Host clientHost = new Host(socket.getInetAddress(), socket.getPort(), socket.getPort());
			DatagramSocket datagramSocket;
			datagramSocket = new DatagramSocket();
			NetOutData udpPortData = new NetOutData();
			System.out.println("Server replying udp port: " + datagramSocket.getLocalPort());
			udpPortData.writeInt(datagramSocket.getLocalPort());
			tcpSocket.send(udpPortData);
			UdpSocket udpSocket = new UdpSocket(datagramSocket, clientHost);
			SingleHostNet newHost = new SingleHostNet(tcpSocket, udpSocket);
			listener.onJoinRequest(newHost);
		}
		catch (SocketException e) {
			System.err.println("Could not create accepted host in joinrRequestHandeler");
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}
	
}
