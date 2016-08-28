package network.baseConnection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import network.TimerThread;

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
			serverSocket.close(); //must do to terminate loop
			//if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
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
			//System.out.println("JoinRequestHandeler terminating because of socket shutDown");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		finally {
			System.out.println("JoinRequestHandeler terminating");
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
			
			//get remote udp port
			//timeout in case client does not send a udp port
			TimerThread requestTimeout = new TimerThread(500, () -> {
				tcpSocket.terminate(); //tcpSocket will close "socket"
				try {
					tcpSocket.join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				throw new IllegalStateException("Server response timeout, could not create connection to server");
			});
			requestTimeout.start();
			
			int responseUdpPort = tcpSocket.preStartWaitForInt(); //wait for response
			requestTimeout.terminate(); //connection made
			System.out.println("Server received udp port: " + responseUdpPort);
			
			Host clientHost = new Host(socket.getInetAddress(), socket.getPort(), responseUdpPort);
			DatagramSocket datagramSocket = new DatagramSocket();
			System.out.println("Server bound udp socket to port: " + datagramSocket.getLocalPort()+". Going to send");
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
