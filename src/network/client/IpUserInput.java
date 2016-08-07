package network.client;

import graphics.Font;
import graphics.GraphicsHandeler;
import rooms.Text;
import userInput.InputState;

import static org.lwjgl.glfw.GLFW.*;

public class IpUserInput {

	

	private GraphicsHandeler graphics;
	


	
	public IpUserInput(GraphicsHandeler graphics) {
		this.graphics = graphics;
	}
	
	
	public String getInput() {
		
		final int[] keys = {GLFW_KEY_0, GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5, GLFW_KEY_6, GLFW_KEY_7,
				GLFW_KEY_8, GLFW_KEY_9, GLFW_KEY_PERIOD};
		final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
		
		final String constText = "Please insert IP adress of the server: ";
		final String finalChar = "|";
		String ipText = "";
		Text currentText = new Text(constText, Font.getStandardFont(), 18, 300f, 100f, 0);

		int lastKey = -1;
		int currKey = 0;
		
		
		while(!glfwWindowShouldClose(graphics.getWindow())) {
			glfwPollEvents();
			
			currentText.setString(constText + ipText + finalChar);
			Text[] texts = {currentText};
			graphics.renderTexts(texts);
			
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
		
		return "";
	}
}
