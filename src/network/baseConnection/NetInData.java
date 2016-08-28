package network.baseConnection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class NetInData {

	
	private ByteArrayInputStream byteIn;
	private DataInputStream dataIn;
	
	
	public NetInData(byte[] bytes) {
		byteIn = new ByteArrayInputStream(bytes);	
		dataIn = new DataInputStream(byteIn);
	}
	
	
	public void clear() {
		try {
			dataIn.reset();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	
	public byte readByte() {
		try {
			return dataIn.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public int readInt() {
		try {
			return dataIn.readInt();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public float readFloat() {
		try {
			return dataIn.readFloat();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public boolean readBoolean() {
		try {
			return dataIn.readBoolean();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("io socket might be closed");
		}
	}
	public String readString() {
		try {
			int strLength = readInt();
			char[] chars = new char[strLength];
			for (int i = 0; i < strLength; i++) {
				chars[i] = dataIn.readChar();
			}
			return new String(chars);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("socket might be closed");
		}
	}
	public String readChars(int length) {
		try {
			char[] chars = new char[length];
			for (int i = 0; i < length; i++) {
				chars[i] = dataIn.readChar();
			}
			return new String(chars);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("socket might be closed");
		}
	}
	
	public int byteSize() {
		try {
			return dataIn.available();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
			return -1;
		}
	}
	
	
	@Override
	public String toString() {
		return "[NetInData, size: "+byteSize()+" bytes]";
	}
}
