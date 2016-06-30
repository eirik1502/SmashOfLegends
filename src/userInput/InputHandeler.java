package userInput;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;



public class InputHandeler {


	private long inputSource;
	private InputState inputState;


	public InputHandeler(long inputSource) {
		this.inputSource = inputSource;
		inputState = new InputState();
		addInputListeners();
	}
	private void addInputListeners() {
		
		glfwSetMouseButtonCallback( inputSource, (window, button, action, mods) -> {
			if (action == GLFW_RELEASE) {
				inputState.setMousePressed(button, false);
			}
			else if (action == GLFW_PRESS) {
				inputState.setMousePressed(button, true);
			}
		});
		glfwSetCursorPosCallback( inputSource, (window, xpos, ypos) -> {
			inputState.setMouseX( xpos );
			inputState.setMouseY( ypos );
		});
        glfwSetKeyCallback(inputSource, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
            	glfwSetWindowShouldClose(window, true);
            }
            if (action == GLFW_RELEASE){
            	inputState.setKeyboardPressed(key, false);
            }
            else if (action == GLFW_PRESS){
            	inputState.setKeyboardPressed(key, true);
            }
        });

	}



    public InputState getState() {
    	return inputState;
    }
}