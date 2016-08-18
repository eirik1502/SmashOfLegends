package game.net;

import java.util.concurrent.ConcurrentLinkedDeque;

import clientGame.net.ClientGameState;
import clientGame.net.FixedSizeDeque;
import clientGame.net.NetSendtData;
import network.baseConnection.NetInData;
import network.baseConnection.NetInDataListener;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

public class SecureUdpNet {

	
	private SingleHostNet connection;
	private int protocolId;
	
	
	private int localSeqNo = 0;
	private FixedSizeDeque<NetSendtData> sendtData = new FixedSizeDeque<>(32);//newest -> oldest (addFirst)
	
	private int remoteSeqNo = -1;
	private FixedSizeDeque<Boolean> ackBitSeq = new FixedSizeDeque<>(32); //newest -> oldest (addFirst)
	
	private NetInDataListener listener;
	
	
	public SecureUdpNet(NetInDataListener listener, SingleHostNet connection, int protocolId) {
		this.listener = listener;
		this.connection = connection;
		this.protocolId = protocolId;
		
		connection.setTcpDataInListener((data) -> System.err.println("Should not receive tcp data in game"));
		connection.setUdpDataInListener(data -> onUdpData(data));
		
		//fill queues
		for (int i = 0; i < sendtData.getFixedSize(); i++) {
			sendtData.add(new NetSendtData());
		}
		for (int i = 0; i < ackBitSeq.getFixedSize(); i++) {
			ackBitSeq.add(true);
		}
	}
	

	public void sendData(NetOutData data) {
		NetOutData outData = getHeader();
		outData.writeData(data);

		checkResend(sendtData.peekLast());
		sendtData.addFirst(new NetSendtData(outData));
		System.out.println("UDPsecure is sending: " + outData); //-----------------------------------------SYSO
		connection.sendUdpData(outData);
	}
	private NetOutData getHeader() {
		NetOutData head = new NetOutData();
		head.writeInt(GameNet.protocolId);
		head.writeInt( getNextSeqNo() );
		head.writeInt( remoteSeqNo );
		head.writeInt( getAckBitSeq() );
		return head;
	}
	
	private void onUdpData(NetInData data) {
		System.out.println("UDPsecure received: " + data); //-----------------------------------------SYSO
		byte readResult = readHeader(data);
		if (readResult == 1) { //ok
			listener.onNetDataReceived(data); //the rest of the data
		}
		else if (readResult == 0) { //packet is outdated, but some of it should maybe be used
			System.err.println("Got outdated data, nothing is read");
		}
		else if (readResult == -1) { //wrong protocol
			System.err.println("Got data of wrong protocol");
		}
		
	}
	
	private void checkResend(NetSendtData data) {
		
	}
	private int getNextSeqNo() {
		return localSeqNo++;
	}
	
	// -1:wrong protocol	0:outdated	1:ok
	private byte readHeader(NetInData in) {
		if (in.readInt() != GameNet.protocolId) return -1;
		int remoteSeqNo = in.readInt();
		int ackSeqNo = in.readInt();
		int ackBitSeq = in.readInt();
		
		ackSendtData(ackSeqNo, ackBitSeq);
		updateRemoteAck(remoteSeqNo);
		
		//if remote SeqNo Did not get updated, its older than the last seqNo
		if (remoteSeqNo < this.remoteSeqNo) return 0;
		return 1;
	}
	
	private int getAckBitSeq() { //convert ackBitSeq to a int bitSeq
		int res = 0;
		
		for (Boolean b: ackBitSeq) { //starting with leading bit
			//if b, set most right bit and push all bits to left
			if (b) {
				res = (res | 0x00000001);
			}
			res = (res << 1);
		}
		return res;
	}
	
	private void ackSendtData(int ackSeqNo, int bitSeq) {
		int firstSeqNo = this.localSeqNo - ackSeqNo;
		//first ackkSeqNo is always acked
		sendtData.get(firstSeqNo).setAcked();
		for (int i = firstSeqNo +1; i < sendtData.size(); i++) {
			if ( (bitSeq & 0x80000000) == 0x80000000) { //if leftMost bit is 1
				sendtData.get(i).setAcked();
			}
			bitSeq = (bitSeq << 1); //push bits to left, so leftMost bit is the next bit
		}
	}
	
	private void updateRemoteAck(int remoteSeqNo) {
		int addSeqNo = remoteSeqNo - this.remoteSeqNo;
		
		if (addSeqNo == 0) {
			System.err.println("Received a remote seqNo that is equal to current remoteSeqNo. Can't happen!?!? packet may have been sendt twice");
		}
		else if (addSeqNo < -33) { //if packet is this old, its out of queue, and possibly got resendt. Throw packet
			throw new IllegalStateException("packet lost up to 32 packet sends later. Packet has been resendt and would be duplicated");
		}
		else if (addSeqNo < 0) { //got outdated data
			int bitSeqIndex = (-addSeqNo) -1; //-1 because index 0 would be the remoteSeqNo
			ackBitSeq.set(bitSeqIndex, true);
		}
		else { //got new data
			//fill spaces until current received ack
			for (int i = 0; i < addSeqNo-1; i++) {
				ackBitSeq.addFirst(false);
			}
			//set the received ack
			this.remoteSeqNo = remoteSeqNo;
		}
		
	}
}
