package application;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


public class TutMain implements Runnable {
	
	private int width = 1600;
	private int height = 900;
	
	private Thread thread;
	private boolean running;
	
	private long window;
	
	
	public void start() {
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
	}
	
	private void init() {
		if (!glfwInit()) {
			System.err.println("Could not initialize GLFW!");
			return;
		}
		
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        
		window = glfwCreateWindow(width, height, "Smash of Legends", NULL, NULL);
		if (window == NULL) {
			System.err.println("Could not create GLFW window!");
			return;
		}
		
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            	glfwSetWindowShouldClose(window, true);
        });
        
     // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2
        );
		
		
		glfwMakeContextCurrent(window);
        glfwSwapInterval(1);// Enable v-sync
		glfwShowWindow(window);
		
		
		GL.createCapabilities();
		
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		
 		glEnable(GL_DEPTH_TEST);
		//glActiveTexture(GL_TEXTURE1);
		//glEnable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		
		/*Shader.loadAll();
		
		Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
		Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BG.setUniform1i("tex", 1);
		
		Shader.BIRD.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BIRD.setUniform1i("tex", 1);
		
		Shader.PIPE.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.PIPE.setUniform1i("tex", 1);
		
		level = new Level();*/
	}
	
	public void run() {
		init();
		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				
				update();
				
				updates++;
				delta--;
			}
			
			render();
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
			if (glfwWindowShouldClose(window))
				running = false;
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
	private void update() {
		glfwPollEvents();
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		int error = glGetError();
		if (error != GL_NO_ERROR)
			System.out.println(error);
		
		glfwSwapBuffers(window);
	}

	public static void main(String[] args) {
		TutMain main = new TutMain();
		main.start();
	}

}
