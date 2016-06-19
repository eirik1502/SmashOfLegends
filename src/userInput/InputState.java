package userInput;

import java.util.HashMap;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class InputState {


    private double mouseX;
    private double mouseY;

    private HashMap<KeyCode, Boolean> keyboardKeyHeld = new HashMap<>();
    private HashMap<MouseButton, Boolean> mouseButtonHeld = new HashMap<>();


    public InputState() {
        mouseX = 0;
        mouseY = 0;

        for (KeyCode keyCode : KeyCode.values()) {
            keyboardKeyHeld.put( keyCode, false );
        }
        for (MouseButton mouseButton : MouseButton.values()) {
            mouseButtonHeld.put( mouseButton, false );
        }
    }


    public double getMouseX() {
        return mouseX;
    }
    public double getMouseY() {
        return mouseY;
    }
    public boolean isMousePressed( MouseButton mouseButton ) {
        return mouseButtonHeld.get(mouseButton);
    }
    public boolean isKeyboardPressed( KeyCode keyCode) {
        return keyboardKeyHeld.get( keyCode );
    }


    void setMouseX( double x ) {
        this.mouseX = x;
    }
    void setMouseY( double y ) {
        this.mouseY = y;
    }

    void setMousePressed( MouseButton mouseButton, boolean value ) {
        mouseButtonHeld.put(mouseButton, value);
    }
    void setKeyboardPressed( KeyCode keyCode, boolean value ) {
        keyboardKeyHeld.put(keyCode, value);
    }


}
