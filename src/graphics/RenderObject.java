package graphics;

import maths.Matrix4f;
import maths.Vector3f;

/**
 *
 * @author Eirik
 * object to render graphics through
 * uses javafx canvas for the moment
 *
 */
public class RenderObject {


	private Shader shader;
	

	public RenderObject() {
		shader = new Shader("shaders/shader.vert", "shaders/shader.frag");
		shader.bind();
		float width = GraphicsHandeler.WIDTH;
		float height = GraphicsHandeler.HEIGHT;
		Matrix4f pr_matrix = Matrix4f.orthographic(0, width, height, 0, -1.0f, 1.0f);
		shader.setUniformMat4f("pr_matrix", pr_matrix);
		shader.unbind();
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
	public void drawSprite(Sprite image, float dx, float dy, float rotation) {

		//double rotationDegree = (180*rotation)/Math.PI;
		VertexArray va = image.getVertexArray();
		Texture tex = image.getTexture();
		float cx = image.getCenterX();
		float cy = image.getCenterX();
		
		shader.bind();
		tex.bind();
		va.bind();
		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(dx, dy, 0.0f)));
		Matrix4f modelTranslation = Matrix4f.translate(new Vector3f(-cx, -cy, 0));
		Matrix4f modelRotation = Matrix4f.rotate(rotation);
		shader.setUniformMat4f("ml_matrix", modelRotation.multiply(modelTranslation)  );
		va.draw();
		
		va.unbind();
		tex.unbind();
		shader.unbind();
		
	}

//	public void setFill( String color ) {
//		renderComponent.setFill( Paint.valueOf(color) );
//	}
//
//	public void fillRect(double x, double y, double w, double h) {
//		renderComponent.fillRect(x, y, w, h);
//	}
//
//	public void fillCircle( double x, double y, double r) {
//		renderComponent.fillOval(x-r, y-r, r*2, r*2);
//	}

}
