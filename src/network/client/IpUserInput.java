package network.client;

import graphics.Font;
import graphics.GraphicsEntity;
import graphics.GraphicsHandeler;
import graphics.Sprite;
import graphics.Text;
import network.ClientGameObjects.ClientEntity;
import userInput.InputState;

import static org.lwjgl.glfw.GLFW.*;

public class IpUserInput extends GraphicsEntity {
	


	public IpUserInput(Sprite sprite, float x, float y, float rotation) {
		super(sprite, x, y, rotation);
		// TODO Auto-generated constructor stub
	}

	private Text[] text = new Text[1];

	

	
	@Override
	public void start() {
		
	}
	
	
	public void update() {
		
	}
	
	public Text[] getTexts() {
		return text;
	}
	
	public String getInput() {
		
		final int[] keys = {GLFW_KEY_0, GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5, GLFW_KEY_6, GLFW_KEY_7,
				GLFW_KEY_8, GLFW_KEY_9, GLFW_KEY_PERIOD};
		final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
		
		final String constText = "Please insert IP adress of the server: ";
		final String finalChar = "|";
		String ipText = "";
		Text currentText = new Text(constText, Font.getStandardFont(), 18, 300f, 100f, 0);
		text[0] = currentText;

		int lastKey = -1;
		int currKey = 0;
		
		
		while(true) {
			
			currentText.setString(constText + ipText + finalChar); //to be rendered

			
			try {
				Thread.sleep(16);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			currKey = 0;
			
			if (InputState.isKeyboardPressed(GLFW_KEY_ENTER)) {
				
				return ipText;
			}
			int keyCode = GLFW_KEY_BACKSPACE;
			if (InputState.isKeyboardPressed(keyCode)) {
				if (lastKey != keyCode && !ipText.isEmpty()) {
					ipText = ipText.substring(0, ipText.length()-1);
					lastKey = keyCode;
				}
				continue;
			}
			else if (lastKey == keyCode) lastKey = -1;
			
			for (int i = 0; i < keys.length; i++) {
				keyCode = keys[i];
				if (InputState.isKeyboardPressed(keyCode) ) {
					if (lastKey != keyCode) {
						char currChar = chars[i];
						ipText += currChar;
						lastKey = keyCode;
					}
					break;
				}
				else if (lastKey == keyCode) lastKey = -1;
			}
			
		}
		
	}
}
