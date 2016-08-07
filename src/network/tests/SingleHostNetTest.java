package network.tests;

import java.io.IOException;
import java.util.ArrayList;

import network.baseConnection.Host;
import network.baseConnection.JoinRequestHandeler;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

public class SingleHostNetTest {

	private JoinRequestHandeler joinHandeler;
	
	private final float[] clientSend = {10.0f, 34f, 2f, -5f };
	private final int[] serverSend = {-13, 63, 2, 9 }; //last two are sent to two clients
	
	private SingleHostNet client1, client2;
	private Host serverHost;
	
	//private boolean finished;
	
	private ArrayList<SingleHostNet> serverConnections = new ArrayList<>();
	
	private int clientSum = 0;
	private float serverSum = 0;
	
	
	public static void main(String[] args) {
		
		SingleHostNetTest shnTest = new SingleHostNetTest();
		TcpSocketTest tcpSocketTest = new TcpSocketTest();
		UdpSocketTest udpSocketTest = new UdpSocketTest();
		
		boolean tcpSockResult = tcpSocketTest.test();
		boolean udpSockResult = udpSocketTest.test();
		boolean shnResult = shnTest.test();

		System.out.println("\n.."+
							"\nSub tests:"+
							"\nTcpSocketTest success: " + tcpSockResult+
							"\nUdpSocketTest success: " + udpSockResult+
							"\n.."+
							"\nActual test:"+
							"\n(also tested JoinRequestHandeler)"+
							"\nSingleHostTest success: " +  shnResult
							);
		
	}
	
	private void setup() {
		serverHost = new Host("127.0.0.1", 9998, -1);
		joinHandeler = new JoinRequestHandeler(serverHost.getTcpPort());
		
	}
	
	public boolean test() {
		setup();
		
		joinHandeler.setListener(newHost -> addServerConnection(newHost));
		joinHandeler.start();
		System.out.println("ready to receive connections");
		
		client1 = SingleHostNet.createServerConnection(serverHost);
		System.out.println("client assign host udp port: " + serverHost.getUdpPort());
		System.out.println("Client1 connected");
		client1.setTcpDataInListener(data -> this.onClientTcpData(data));
		client1.setUdpDataInListener(data -> this.onClientUdpData(data));
		client1.start();
		
		NetOutData outData = new NetOutData();
		outData.writeFloat(clientSend[0]);
		client1.sendTcpData(outData);
		System.out.println("Client1 sendt tcp data 0");
		
		outData = new NetOutData();
		outData.writeInt(serverSend[0]);
		for (SingleHostNet hostNet : serverConnections) hostNet.sendTcpData(outData);
		System.out.println("Server sendt to all clients tcp data 0");
		
		outData = new NetOutData();
		outData.writeFloat(clientSend[1]);
		client1.sendUdpData(outData);
		System.out.println("Client1 sendt udp data 1");
		
		outData = new NetOutData();
		outData.writeInt(serverSend[1]);
		for (SingleHostNet hostNet : serverConnections) hostNet.sendUdpData(outData);
		System.out.println("Server sendt to all clients udp data 1");
		
		client2 = SingleHostNet.createServerConnection(serverHost);
		System.out.println("client connected");
		client2.setTcpDataInListener(data -> this.onClientTcpData(data));
		client2.setUdpDataInListener(data -> this.onClientUdpData(data));
		client2.start();
		System.out.println("Client 2 connected");
		
		outData = new NetOutData();
		outData.writeFloat(clientSend[2]);
		client2.sendUdpData(outData);
		System.out.println("Client2 sendt udp data 2");
		
		outData = new NetOutData();
		outData.writeInt(serverSend[2]);
		for (SingleHostNet hostNet : serverConnections) hostNet.sendTcpData(outData);
		System.out.println("Server sendt to all clients tcp data 2");
		
		outData = new NetOutData();
		outData.writeFloat(clientSend[3]);
		client2.sendTcpData(outData);
		System.out.println("Client2 sendt tcp data 3");
		
		outData = new NetOutData();
		outData.writeInt(serverSend[3]);
		for (SingleHostNet hostNet : serverConnections) hostNet.sendUdpData(outData);
		System.out.println("Server sendt to all clients udp data 3");
	
		
		
		float actualServerSum = 0;
		int actualClientSum = 0;
		for (float f : clientSend) actualServerSum += f;
		for (int i : serverSend) actualClientSum += i;
		//last two data will be sendt to both clients
		actualClientSum += serverSend[2];
		actualClientSum += serverSend[3];

		
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			System.err.println("Thread interrupted");
			e.printStackTrace();
		}
		
		boolean result = true;
		if (clientSum == actualClientSum) {
			System.out.println("Client sum is right!");
		}
		else result = false;
		
		if (serverSum == actualServerSum) {
			System.out.println("Server sum is right!");
		}
		else result = false;
		
		if (result) System.out.println("Single host net test: success");
		else {
			System.out.println("Server recieved: "+serverSum+" should be: "+actualServerSum);
			System.out.println("Client recieved: "+clientSum+" should be: "+actualClientSum);
			System.out.println("Test again, network instability might be the fail reason");
			System.out.println("Single host net test: failed");
		}
		

		
		serverConnections.forEach(hostConnection -> hostConnection.terminate());
		joinHandeler.terminate();
		client1.terminate();
		client2.terminate();
		//System.out.println("Threads active: "+Thread.activeCount());

//		try {
//			joinHandeler.join();
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		return result;
	}
	
	private void addServerConnection(SingleHostNet host) {
		host.setTcpDataInListener(data->onServerTcpData(data));
		host.setUdpDataInListener(data->onServerUdpData(data));
		serverConnections.add(host);
		host.start();
	}
	private void onServerTcpData(NetInData data) {
		System.out.println("Server got tcp data!");
		onServerData(data);
	}
	private void onServerUdpData(NetInData data) {
		System.out.println("Server got udp data!");
		onServerData(data);
	}
	private void onServerData(NetInData data) {
		serverSum += data.readFloat();
	}
	
	
	private void onClientTcpData(NetInData data) {
		System.out.println("Client got tcp data!");
		onClientData(data);
	}
	private void onClientUdpData(NetInData data) {
		System.out.println("Client got udp data!");
		onClientData(data);
	}
	private void onClientData(NetInData data) {
		clientSum += data.readInt();
	}
	
}
