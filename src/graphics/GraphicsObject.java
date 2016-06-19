package graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 *
 * @author Eirik
 * object to render graphics through
 * uses javafx canvas for the moment
 *
 */
public class GraphicsObject {


	private GraphicsContext renderComponent;
	private double width;
	private double height;



	public GraphicsObject( GraphicsContext graphicsContext, double width, double height ) {
		renderComponent = graphicsContext;
		this.width = width;
		this.height = height;
	}

	public void clear() {
		renderComponent.clearRect(0,0, width, height);
	}

	public void setFill( String color ) {
		renderComponent.setFill( Paint.valueOf(color) );
	}

	public void fillRect(double x, double y, double w, double h) {
		renderComponent.fillRect(x, y, w, h);
	}

	public void fillCircle( double x, double y, double r) {
		renderComponent.fillOval(x-r, y-r, r*2, r*2);
	}

}
