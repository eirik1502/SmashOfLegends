package network.baseConnection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

import network.TimerThread;

public class SingleHostNet {

	
	private TcpSocket tcpSocket;
	private UdpSocket udpSocket;
	
	private NetInDataListener inTcpDataListener, inUdpDataListener;
	
	private boolean running = true;
	
	
	public SingleHostNet(TcpSocket tcpSocket, UdpSocket udpSocket) {
		this.tcpSocket = tcpSocket;
		this.udpSocket = udpSocket;
	}
	
	public static SingleHostNet createServerConnection(Host serverHost) { //host does not need udp port set
		try {
			Socket socket = new Socket(serverHost.getAddress(), serverHost.getTcpPort()); //connecting
			DatagramSocket datagramSocket = new DatagramSocket(socket.getLocalPort()); //assign tcp local port as udp local port
			TcpSocket tcpSocket = new TcpSocket(socket);
			
			//timeout in case server do not answer
			TimerThread requestTimeout = new TimerThread(500, () -> {
				datagramSocket.close(); //UdpSocket isnt created yet
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
			System.out.println("Client received udp port: " + responseUdpPort);
			
			serverHost.setUdpPort(responseUdpPort);
			//datagramSocket.connect(socket.getInetAddress(), responseUdpPort);
			UdpSocket udpSocket = new UdpSocket(datagramSocket, serverHost);
			return new SingleHostNet(tcpSocket, udpSocket);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		throw new IllegalStateException("could not create server connection");
	}
	
	public void start() {
		if (inTcpDataListener == null || inUdpDataListener == null) throw new IllegalStateException("input data listeners must be set before starting singleHostNet");
		tcpSocket.setDataInListener(data -> onTcpDataReceive(data));
		udpSocket.setDataInListener(data -> onUdpDataReceive(data));
		tcpSocket.start();
		udpSocket.start();
	}
	public void terminate() {
		running = false;//doesnt do anything by now
		System.out.println("terminating single host net");
		tcpSocket.terminate();
		udpSocket.terminate();
		
		try {
			tcpSocket.join();
			udpSocket.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setTcpDataInListener(NetInDataListener listener) {
		this.inTcpDataListener = listener;
	}
	public synchronized void setUdpDataInListener(NetInDataListener listener) {
		this.inUdpDataListener = listener;
	}
	
	public synchronized void sendTcpData(NetOutData data) {
		tcpSocket.send(data);
	}
	public synchronized void sendUdpData(NetOutData data) {
		udpSocket.send(data);
	}
	
	private synchronized void onTcpDataReceive(NetInData data) {
		this.inTcpDataListener.onNetDataReceived(data);
	}
	private synchronized void onUdpDataReceive(NetInData data) {
		
		this.inUdpDataListener.onNetDataReceived(data);
	}
}
