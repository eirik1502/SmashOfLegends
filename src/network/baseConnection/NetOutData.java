package network.baseConnection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class NetOutData {

	
	private ByteArrayOutputStream byteOut;
	private DataOutputStream dataOut;
	
	
	public NetOutData() {
		byteOut = new ByteArrayOutputStream(32);	
		dataOut = new DataOutputStream(byteOut);
	}
	
	
	public void clear() {
		try {
			dataOut.flush();
			byteOut.reset();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(NetOutData data) {
		try {
			dataOut.write(data.getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeByte(byte b) {
		try {
			dataOut.writeByte(b);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public void writeBytes(byte[] bytes) {
		try {
			dataOut.write(bytes);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeInt(int i) {
		try {
			dataOut.writeInt(i);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public void writeFloat(float f) {
		try {
			dataOut.writeFloat(f);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public void writeBoolean(boolean b) {
		try {
			dataOut.writeBoolean(b);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public void writeString(String s) {
		try {
			writeInt(s.length());
			dataOut.writeChars(s);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeChars(String s) {
		try {
			dataOut.writeChars(s);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte getByteSize() {
		return (byte)byteOut.size();
	}
	public byte[] getBytes() {
		return byteOut.toByteArray();
	}
	
	public String toString() {
		byte[] bytes = getBytes();
		return Arrays.toString(bytes);
	}
}
