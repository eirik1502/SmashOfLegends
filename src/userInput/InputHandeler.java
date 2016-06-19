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
			inputState.setMousePressed( event.getButton(), true );
		});
		inputSource.setOnMouseReleased( (event) -> {
			inputState.setMousePressed( event.getButton(), false);
		});
		inputSource.setOnMouseMoved( event -> {
			inputState.setMouseX( event.getSceneX() );
			inputState.setMouseY( event.getSceneY() );
		});


	}


    public InputState getState() {
    	return inputState;
    }
}