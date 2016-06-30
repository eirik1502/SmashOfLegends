package userInput;


public class InputState {


    private double mouseX;
    private double mouseY;

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


    public double getMouseX() {
        return mouseX;
    }
    public double getMouseY() {
        return mouseY;
    }
    public boolean isMousePressed( int mouseButton ) {
        return mouseButtonHeld[mouseButton];
    }
    public boolean isKeyboardPressed( int keyCode) {
        return keyHeld[keyCode];
    }


    void setMouseX( double x ) {
        this.mouseX = x;
    }
    void setMouseY( double y ) {
        this.mouseY = y;
    }

    void setMousePressed( int mouseButton, boolean value ) {
        mouseButtonHeld[mouseButton] = value;
    }
    void setKeyboardPressed( int keyCode, boolean value ) {
        keyHeld[keyCode] = value;
    }


}
