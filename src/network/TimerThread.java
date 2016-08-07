package network;

public class TimerThread extends Thread {

	private int timerCheckRate;
	private int timerLength;
	
	private int timePassed = 0;
	
	private EmptyActionListener listener;
	
	private boolean running = true;
	
	
	public TimerThread(int length, EmptyActionListener listener) {
		this(length, listener, 100);
	}
	public TimerThread(int length, EmptyActionListener listener, int checkRate) {
		timerCheckRate = checkRate;
		this.listener = listener;
		timerLength = length;
	}
	
	
	public synchronized void reset() {
		timePassed = 0;
	}
	public synchronized void terminate() {
		running = false;
		try {
			this.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// Put the timer to sleep
		while(running) {
			try
			{ 
				Thread.sleep(timerCheckRate);
			}
			catch (InterruptedException ioe) 
			{
				continue;
			}
	
			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				timePassed += timerCheckRate;
	
				// Check to see if the time has been exceeded
				if (timePassed > timerLength)
				{
					if (running) {
						// Trigger a timeout
						listener.onAction();
						if (timePassed > timerLength)
							break;
					}
				}
			}
		}
	}
	
}
