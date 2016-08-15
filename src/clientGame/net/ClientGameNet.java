package clientGame.net;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

import game.net.GameNet;
import network.baseConnection.NetInData;
import network.baseConnection.NetOutData;
import network.baseConnection.SingleHostNet;

public class ClientGameNet {

	
	private int bufferedInputCount = 0;
	
	private SingleHostNet connection;
	
	private int localSeqNo = 0;
	private int remoteSeqNo = 0;
	//private int ackBitSeq = 0xffffffff;
	private FixedSizeDeque<Boolean> remoteAckBitSeq = new FixedSizeDeque<>(32); //add to head (addFirst)
	
	private FixedSizeDeque<NetSendtData> sendtData = new FixedSizeDeque<>(32);//should be 32b size
	
	private ClientGameState lastState = new ClientGameState();
	
	
	private ConcurrentLinkedDeque<NetInData> inDataBuffer = new ConcurrentLinkedDeque<>();
	
	
	public ClientGameNet(SingleHostNet connection) {
		this.connection = connection;
		connection.setTcpDataInListener(data -> System.err.println("Do not support TCP data ingame"));
		connection.setUdpDataInListener(data -> onUdpData(data));
	}
	
	
	public void sendInputState(boolean keyLeft, boolean keyRight, boolean keyUp, boolean keyDown,
							boolean keyAction1, boolean keyAction2, boolean keyAction3, boolean keyAction4,
							float mouseX, float mouseY) {
		NetOutData out = new NetOutData();
		writeHeader(out);
		out.writeBoolean(keyLeft);
		out.writeBoolean(keyRight);
		out.writeBoolean(keyUp);
		out.writeBoolean(keyDown);
		out.writeBoolean(keyAction1);
		out.writeBoolean(keyAction2);
		out.writeBoolean(keyAction3);
		out.writeBoolean(keyAction4);
		sendData(out);
		
		NetSendtData oldestSendtData = sendtData.poll();
		if (oldestSendtData.isAcked()) {
			//addResendData
		}
		
		sendtData.add(new NetSendtData(out));
	}
	
	public ClientGameState pollInData() {
		int bufferCount = inDataBuffer.size();
		int pollCount = bufferCount-bufferedInputCount;
		NetInData newData = null;
		for (int i = 0; i < pollCount; i++) {
			newData = inDataBuffer.poll();
		}
		
		handleInData(newData);
	}
	public void handleInData(NetInData data) {
		
	}
	
	
	private void onUdpData(NetInData data) {
		inDataBuffer.add(data);
	}
	
	
	public void writeHeader(NetOutData out) {
		out.writeInt(GameNet.protocolId);
		out.writeInt(localSeqNo);
		out.writeInt(remoteSeqNo);
		out.writeInt(ackBitSeq);
	}
	
	// -1:wrong protocol	0:outdated	1:ok
	private byte readHeader(NetInData in) {
		if (in.readInt() != GameNet.protocolId) return false;
		int remoteSeqNo = in.readInt();
		int ackSeqNo = in.readInt();
		int ackBitSeq = in.readInt();
		
		ackLocalSendtData(ackSeqNo, ackBitSeq);
		updateRemoteAck(remoteSeqNo);
		
		if (remoteSeqNo <= this.remoteSeqNo) return 0;
	}
	
	
	private void ackLocalSendtData(int ackSeqNo, int bitSeq) {
		int firstSeqNo = this.localSeqNo - ackSeqNo;
		//first ackkSeqNo is always acked
		sendtData.get(firstSeqNo).setAcked();
		for (int i = firstSeqNo +1; i < sendtData.size(); i++) {
			if ( (bitSeq & 0x00000001) == 1) { //10000...0
				sendtData.get(i).setAcked();
			}
			bitSeq = (bitSeq << 1); 
		}
	}
	
	private void updateRemoteAck(int remoteSeqNo) {
		int addSeqNo = remoteSeqNo - this.remoteSeqNo;
		
		if (addSeqNo <= 0) { //got outdated data
			int bitSeqIndex = -addSeqNo;
			if (!remoteAckBitSeq.get(bitSeqIndex)) remoteAckBitSeq.set(bitSeqIndex, true);
		}
		else { //got new data
			//fill spaces until current received ack
			for (int i = 0; i < addSeqNo-1; i++) {
				remoteAckBitSeq.addFirst(false);
			}
			//set the received ack
			remoteAckBitSeq.addFirst(true);
		}
		
	}
	
	private int getRemoteAckBitSeq() {
		int res = 0;
		
		int i = 0;
		for (Boolean b: remoteAckBitSeq) { //first to last
			if (b) {
				res = (res | 0x80000000);
			}
			res = (res >> 1);
			i++;
		}
		return res;
	}
	
	private void addDataToResend(ResendData data) {
		
	}
}
