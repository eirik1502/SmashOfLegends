package network;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import graphics.Font;
import graphics.GraphicsHandeler;
import graphics.GraphicsUtils;
import graphics.Text;
import network.clientLobby.ChatMessageContainer;

public class TextBoxTest {

	private static int windowWidth = 800, windowHeight = 800;
	private ChatMessageContainer messageContainer;
	private GraphicsHandeler graphics;



	public static void main(String[] args) {
		TextBoxTest test = new TextBoxTest();
		test.test();
	}
	
	
	public boolean test() {
		long window = GraphicsUtils.createWindowOpenGl(windowWidth, windowHeight, "Text box test");
		graphics = new GraphicsHandeler(window, windowWidth, windowHeight);
		
		TextBox box1 = new TextBox(50, 50, 100, Font.getStandardFont(), 18);
		TextBox box2 = new TextBox(550, 150, 250, Font.getStandardFont(), 18);
		TextBox box3 = new TextBox(200, 300, 350, Font.getStandardFont(), 18);
		
		box1.setText("This text box is at 50,50 and has width 100 Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. ");
		box2.setText("This text box is at 500,200 and has width 250 Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean ma");
		box3.setText("This text box i\ns at 200,300 and has width 400 Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean ma");
		
		Text[] texts = { box1.getGraphicsText(), box2.getGraphicsText(), box3.getGraphicsText()};
		graphics.renderTexts(texts);
		
		try {
			Thread.sleep(1500);
			
			box1.addText("height of this box was calculated to be: "+box1.getHeight()+" lineCount: "+box1.getLineCount());
			box2.addText("height of this box was calculated to be: "+box2.getHeight()+" lineCount: "+box2.getLineCount());
			box3.addText("height of this box was calculated to be: "+box3.getHeight()+" lineCount: "+box3.getLineCount());
			
			graphics.renderTexts(texts);

			
			Thread.sleep(60000);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
		
		return true;
	}
}