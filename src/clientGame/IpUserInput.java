package clientGame;

import graphics.Font;
import graphics.GraphicsEntity;
import graphics.GraphicsHandeler;
import graphics.Sprite;
import graphics.Text;
import network.ClientGameObjects.ClientEntity;
import rooms.Room;
import rooms.RoomEntity;
import userInput.InputState;

import static org.lwjgl.glfw.GLFW.*;

public class IpUserInput extends RoomEntity {
	

	private IpUserInputListener listener;
	
	private Text text;
	private String ipString;
	private boolean finished = false;
	
	private int[] keys;
	private char[] chars;

	private String constString;
	private char finalChar;
	private int lastKey;
	private int currKey;
	
	
	public IpUserInput(IpUserInputListener listener) {
		this.listener = listener;
	}
	
	
	@Override
	public void start(Room r) {
		int[] nkeys = {GLFW_KEY_0, GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5, GLFW_KEY_6, GLFW_KEY_7,
				GLFW_KEY_8, GLFW_KEY_9, GLFW_KEY_PERIOD};
		char[] nchars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
		
		keys = nkeys;
		chars = nchars;
		
		lastKey = -1;
		currKey = 0;
		
		ipString = "";
		constString = "Please insert IP adress of the server: ";
		finalChar = '|';
		text = new Text(constString, Font.getStandardFont(), 18, 300f, 100f, 0, 0.1f);
		
		super.addText(text);
		Sprite s = new Sprite("res/Equinox.png", 1, 0f,0f, 100f, 200f, 0f, 0f);
		
		super.addSprite(s);
	}
	
	public void callListener() {
		listener.onInput(this);
	}
	public String getResult() {
		if (!finished) throw new IllegalStateException("Wait for this to finish before trying to fetch result");
		return ipString;
	}
	public void reset(String resetString) {
		finished = false;
		ipString = "";
		constString += "(" + resetString + ")";
	}
	
	public void update() {
		
		if (finished) {
			callListener();
			return;
		}
		text.setString(constString + ipString + finalChar); //to be rendered
		
		currKey = 0;
		
		int keyCode = GLFW_KEY_ENTER;
		if (InputState.isKeyboardPressed(GLFW_KEY_ENTER)) {
			if (lastKey != keyCode && !ipString.isEmpty()) {
				finished = true;
				lastKey = keyCode;
			}
			return;
		}
		else if (lastKey == keyCode) lastKey = -1; //so a key can't be held
		
		keyCode = GLFW_KEY_BACKSPACE;
		if (InputState.isKeyboardPressed(keyCode)) {
			if (lastKey != keyCode && !ipString.isEmpty()) {
				ipString = ipString.substring(0, ipString.length()-1);
				lastKey = keyCode;
			}
			return;
		}
		else if (lastKey == keyCode) lastKey = -1; //so a key can't be held
		
		for (int i = 0; i < keys.length; i++) {
			keyCode = keys[i];
			if (InputState.isKeyboardPressed(keyCode) ) {
				if (lastKey != keyCode) {
					char currChar = chars[i];
					ipString += currChar;
					lastKey = keyCode;
				}
				break;
			}
			else if (lastKey == keyCode) lastKey = -1;
		}
		
	}

}
