package graphics;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import maths.Matrix4f;
import maths.Vector3f;
import utils.ShaderUtils;



public class Shader {
	
	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 1;
	
	//public static Shader BG, BIRD, PIPE, FADE;
	
	private boolean enabled = false;
	
	private final int ID;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	
	
	public Shader(String vertexPath, String fragmentPath) {
		ID = ShaderUtils.load(vertexPath, fragmentPath);
	}
	
	/*public static void loadAll() {
		BG = new Shader("shaders/bg.vert", "shaders/bg.frag");
		BIRD = new Shader("shaders/bird.vert", "shaders/bird.frag");
		PIPE = new Shader("shaders/pipe.vert", "shaders/pipe.frag");
		FADE = new Shader("shaders/fade.vert", "shaders/fade.frag");
	}*/
	
	public int getUniformLocation(String name) {
		if (locationCache.containsKey(name))
			return locationCache.get(name);
		
		int result = glGetUniformLocation(ID, name);
		if (result == -1) 
			System.err.println("Could not find uniform variable '" + name + "'!");
		else
			locationCache.put(name, result);
		return result;
	}
	
	public void setUniform1i(String name, int value) {
		if (!enabled) throw new IllegalStateException("Trying to use a shader while it is disabled");
		glUniform1i(getUniformLocation(name), value);
	}
	
	public void setUniform1f(String name, float value) {
		if (!enabled) throw new IllegalStateException("Trying to use a shader while it is disabled");
		glUniform1f(getUniformLocation(name), value);
	}
	
	public void setUniform2f(String name, float x, float y) {
		if (!enabled) throw new IllegalStateException("Trying to use a shader while it is disabled");
		glUniform2f(getUniformLocation(name), x, y);
	}
	
	public void setUniform3f(String name, Vector3f vector) {
		if (!enabled) throw new IllegalStateException("Trying to use a shader while it is disabled");
		glUniform3f(getUniformLocation(name), vector.x, vector.y, vector.z);
	}
	
	public void setUniformMat4f(String name, Matrix4f matrix) {
		if (!enabled) throw new IllegalStateException("Trying to use a shader while it is disabled");
		glUniformMatrix4fv(getUniformLocation(name), false, matrix.toFloatBuffer());
	}
	
	public void bind() {
		glUseProgram(ID);
		enabled = true;
	}
	
	public void unbind() {
		glUseProgram(0);
		enabled = false;
	}

}
