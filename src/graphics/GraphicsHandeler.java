package graphics;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;


public class GraphicsHandeler {


	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;

	private long window;

	
	private RenderObject renderObject;

	
	private ArrayList<Renderable> renderables = new ArrayList<>();
	

	
	public GraphicsHandeler() {
	}
	

	public long init() {
		
		
		if (!glfwInit()) {
			System.err.println("Could not initialize GLFW!");
			return -1;
		}
		
		setupWindow();
		
		GL.createCapabilities(); //get opengl context
		
		glClearColor(0.2f, 0.3f, 0.6f, 1.0f);
		
 		glEnable(GL_DEPTH_TEST);
		//glActiveTexture(GL_TEXTURE1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		
		renderObject = new RenderObject();
		
		
		return window;
	}


	public void start() {
		
		glfwShowWindow(window);

	}

			
	public void render() {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		for (Renderable renderable : renderables) {
			renderable.render( renderObject );
		}
		
		glfwSwapBuffers(window);
		
		int error = glGetError();
		if (error != GL_NO_ERROR)
			System.err.println("GL error: " + error);
	}
	
	private void setupWindow() {
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        
		window = glfwCreateWindow(WIDTH, HEIGHT, "Smash of Legends", NULL, NULL);
		if (window == NULL) {
			System.err.println("Could not create GLFW window!");
			return;
		}
		
        
     // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - WIDTH) / 2,
            (vidmode.height() - HEIGHT) / 2
        );
		
		
		glfwMakeContextCurrent(window);
        glfwSwapInterval(1);// Enable v-sync
	}

	public void addRenderable( Renderable object ) {
		renderables.add(object);
	}
	public void removeRenderable( Renderable object ) {
		renderables.remove(object);
	}


}
