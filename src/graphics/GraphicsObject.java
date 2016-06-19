package graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;

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

	/**
	 *
	 * @param image
	 * @param cx
	 * @param cy
	 * @param ox
	 * @param oy
	 * @param rotation
	 * ox, oy is the offset for the center of image. rotation is in radians
	 */
	public void drawImage(Image image, double cx, double cy, double ox, double oy, double rotation) {
		GraphicsContext rc = renderComponent;
		rc.save();

		double rotationDegree = (180*rotation)/Math.PI;
		Rotate r = new Rotate(rotationDegree, cx+ox, cy+oy);
        rc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		//rc.translate(-ox, -oy);
		//rc.rotate( (180*rotation)/Math.PI );
		rc.drawImage(image, cx, cy);
		rc.restore();
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
