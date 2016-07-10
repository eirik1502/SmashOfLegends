package userInput;


public class InputState {


    private float mouseX;
    private float mouseY;

    private boolean[] keyHeld = new boolean[256*2];
    private boolean[] mouseButtonHeld = new boolean[16];


    public InputState() {
        mouseX = 0;
        mouseY = 0;

        for (int i = 0; i < keyHeld.length; i++) {
           keyHeld[i] = false;
        }
        for (int i = 0; i < mouseButtonHeld.length; i++) {
            mouseButtonHeld[i] = false;
         }
    }


    public float getMouseX() {
        return mouseX;
    }
    public float getMouseY() {
        return mouseY;
    }
    public boolean isMousePressed( int mouseButton ) {
        return mouseButtonHeld[mouseButton];
    }
    public boolean isKeyboardPressed( int keyCode) {
        return keyHeld[keyCode];
    }


    void setMouseX( float x ) {
        this.mouseX = x;
    }
    void setMouseY( float y ) {
        this.mouseY = y;
    }

    void setMousePressed( int mouseButton, boolean value ) {
        mouseButtonHeld[mouseButton] = value;
    }
    void setKeyboardPressed( int keyCode, boolean value ) {
        keyHeld[keyCode] = value;
    }


}
