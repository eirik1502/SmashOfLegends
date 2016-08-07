package network.baseConnection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TcpSocket extends Thread {
	
	
	private Socket socket;
	
	//private DataOutputStream outStream;
	//private DataInputStream inStream;
	private BufferedOutputStream out;
	private BufferedInputStream in;
	
	private NetInDataListener inputListener;
	
	private boolean running = true;
	
	
//	public TcpSocket(String address, int port) {
//		socket = new Socket();
//		setup();
//	}
	public TcpSocket(Socket socket) {
		this.socket = socket;
		setup();
	}
	private void setup() {
		try {
//			outStream = new DataOutputStream(socket.getOutputStream());
//			inStream = new DataInputStream(socket.getInputStream());
			out = new BufferedOutputStream( socket.getOutputStream() );
			in = new BufferedInputStream( socket.getInputStream() );
		}
		catch (IOException e) {
			System.err.println("could not create input/output stream or socket isn't connected");
			e.printStackTrace();
		}
		
	}
	
	public int preStartWaitForInt() {
		try {
			//in.read(); //leading msg size byte
			DataInputStream dataIn = new DataInputStream(socket.getInputStream());
			dataIn.readByte();//leading msg size byte
			return dataIn.readInt();
		}
		catch (SocketException e) {
			System.err.println("waiting for data while socket closed");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return -1;

	}
	
	public synchronized void setDataInListener(NetInDataListener listener) {
		this.inputListener = listener;
	}
	
	@Override
	public void run() {
		if (inputListener == null) throw new IllegalStateException("input data listener must be set before starting tcp socket");
		seekInput();
	}
	
	public synchronized void terminate() {
		running = false;
		try {
			socket.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public synchronized void send(NetOutData data) {
		try {
			byte msgSize = data.getByteSize();
			out.write(msgSize);
			out.write(data.getBytes(), 0, msgSize);
			out.flush();
		}
		catch (IOException e) {
			System.err.println("problem with sending data in tcp socket");
			e.printStackTrace();
		}
	}
	
	private void onInput(byte[] bytes) {
		inputListener.onNetDataReceived(new NetInData(bytes));
	}
	
	private void seekInput() {
		byte[] bytes;
		
		try {
			
			while(running) {
				
				int byteLen = in.read(); //stop and wait
				if (byteLen < 0) break; //socket is probably closed
				//System.out.println("tcp read byte size: "+byteLen);
				
				
				bytes = new byte[ byteLen ];
				int bytesRead = in.read(bytes, 0, bytes.length);
				//System.out.println("tcp actually read: "+bytesRead);
				onInput(bytes);
			}
		
		}
		catch (SocketException e) {
			//if socket is closed while thread is waiting fr read. Pass to finally
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
		finally {
			System.out.println("TcpSocket has closed");
			try {
				in.close();
				out.close();
				if (socket != null && !socket.isClosed()) socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	

}
