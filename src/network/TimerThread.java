package network;

public class TimerThread extends Thread {

	private int timerCheckRate;
	private int timerLength;
	
	private int timePassed = 0;
	
	private EmptyActionListener listener;
	
	
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
	
	@Override
	public void run() {
		// Put the timer to sleep
		while(true) {
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
					// Trigger a timeout
					listener.onAction();
					if (timePassed > timerLength)
						break;
				}
			}
		}
	}
	
}
