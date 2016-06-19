package userInput;

import javafx.scene.canvas.Canvas;

public class InputHandeler {


	private Canvas inputSource;
	private InputState inputState;


	public InputHandeler(Canvas inputSource) {
		this.inputSource = inputSource;
		inputState = new InputState();
		inputSource.requestFocus(); //focus for input
		addInputListeners();
	}
	private void addInputListeners() {

		inputSource.setOnKeyPressed( (event) -> {
			inputState.setKeyboardPressed( event.getCode(), true );
		});
		inputSource.setOnKeyReleased( (event) -> {
			inputState.setKeyboardPressed( event.getCode(), false);
		});


		inputSource.setOnMousePressed( (event) -> {
			setStateMouse(event.getSceneX(), event.getSceneY());
			inputState.setMousePressed( event.getButton(), true );

		});
		inputSource.setOnMouseReleased( (event) -> {
			setStateMouse(event.getSceneX(), event.getSceneY());
			inputState.setMousePressed( event.getButton(), false);

		});
		inputSource.setOnMouseMoved( event -> {
			setStateMouse(event.getSceneX(), event.getSceneY());
		});


	}

	private void setStateMouse(double x, double y) {
		inputState.setMouseX( x );
		inputState.setMouseY( y );
	}


    public InputState getState() {
    	return inputState;
    }
}