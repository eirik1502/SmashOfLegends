package graphics;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphicsHandeler {


	public static final double WIDTH = 1600;
	public static final double HEIGHT = 900;

	private Group root;
	private Canvas canvas;
	private GraphicsObject graphicsObject;

	private ArrayList<Renderable> renderables = new ArrayList<>();

	public GraphicsHandeler() {
	}

	public Canvas init() {
		root = new Group();
		setupCanvas();
		root.getChildren().add(canvas);
		graphicsObject = new GraphicsObject( canvas.getGraphicsContext2D(), WIDTH, HEIGHT );
		return canvas;
	}

	private void setupCanvas( ) {
		Canvas canvas = new Canvas();
		canvas.setWidth(WIDTH);
		canvas.setHeight(HEIGHT);
		this.canvas = canvas;
	}

	public void start(Stage primaryStage) {
		try {
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		graphicsObject.clear();
		for (Renderable renderable : renderables) {
			renderable.render( graphicsObject );
		}
	}

	public void addRenderable( Renderable object ) {
		renderables.add(object);
	}


}
