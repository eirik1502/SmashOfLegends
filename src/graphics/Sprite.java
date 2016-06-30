package graphics;

public class Sprite {

	
	private VertexArray vertexArray;
	private Texture texture;
	
	private float depth;
	private float width;
	private float height;
	
	private float centerX;
	private float centerY;
	
	
	public Sprite(String imageFilename, float centerX, float centerY) {
		texture = new Texture( imageFilename);
		width = texture.getWidth();
		height = texture.getHeight();
		depth = 0;
		this.centerX = centerX;
		this.centerY = centerY;
		vertexArray = getRectangleVertexArray( width, height, depth);
	}
	
	public VertexArray getVertexArray() {
		return vertexArray;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public static VertexArray getRectangleVertexArray(float width, float height, float depth) {
		
		float[] vertices = new float[] {
			0.0f, 0.0f, depth,
			0.0f, height, depth,
			width, height, depth,
			width, 0.0f, depth
		};
			
		byte[] indices = new byte[] {
			0, 1, 2,
			2, 3, 0
		};
		
		float[] tcs = new float[] { //texture coordinates
			0, 0,
			0, 1,
			1, 1,
			1, 0
		};
		
		return new VertexArray( vertices, indices, tcs );
	}
	
	public float getCenterX() {
		return centerX;
	}
	public float getCenterY() {
		return centerY;
	}
}
