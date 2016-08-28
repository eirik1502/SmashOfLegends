package network.baseConnection;

import java.io.IOException;
import java.net.ConnectException;
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
	
	public static SingleHostNet createServerConnection(Host serverHost){ //host does not need udp port set, will be set after this
		try {
			Host myServerHost = new Host(serverHost.getAddress(), serverHost.getTcpPort(), -1);
			
			Socket socket = new Socket(myServerHost.getAddress(), myServerHost.getTcpPort()); //connecting
			DatagramSocket datagramSocket = new DatagramSocket();
			//if more than one socket is created with same port, we cannot use that as udp port on a single machine
			TcpSocket tcpSocket = new TcpSocket(socket);
			
			NetOutData udpPortData = new NetOutData();
			System.out.println("Client sending udp port: " + datagramSocket.getLocalPort());
			udpPortData.writeInt(datagramSocket.getLocalPort());
			tcpSocket.send(udpPortData); //sending udp port data
			
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
			
			myServerHost.setUdpPort(responseUdpPort);
			//datagramSocket.connect(socket.getInetAddress(), responseUdpPort);
			UdpSocket udpSocket = new UdpSocket(datagramSocket, myServerHost);
			return new SingleHostNet(tcpSocket, udpSocket);
		}
		catch (ConnectException e) {
			return null; //connection could not be made
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
	
	
	public TcpSocket getTcpSocket() {
		return tcpSocket;
	}
	public UdpSocket getUdpSocket() {
		return udpSocket;
	}
}
