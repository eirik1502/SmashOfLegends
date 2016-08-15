package network.clientLobby;

import graphics.GraphicsHandeler;
import graphics.GraphicsUtils;
import graphics.Text;
import network.ChatMessage;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class ChatMessageContainerTest {

	
	private static int windowWidth = 500, windowHeight = 800;
	private ChatMessageContainer messageContainer;
	private GraphicsHandeler graphics;
	
	
	public static void main(String[] args) {
		ChatMessageContainerTest test = new ChatMessageContainerTest();
		test.test();
	}
	
	
	public boolean test() {
		long window = GraphicsUtils.createWindowOpenGl(windowWidth, windowHeight, "Chat message container test");
		graphics = new GraphicsHandeler(window, windowWidth, windowHeight);
		
		ChatMessageContainer container = new ChatMessageContainer(0, 0, 400, 200);
		ChatMessage msg1 = new ChatMessage(null, "hello, now I know wauiphsf esfy spiff 9fu poefjsd fsudf ");
		ChatMessage msg2 = new ChatMessage(null, "cool");
		ChatMessage msg3 = new ChatMessage(null, "how are you? rgøh  iorhg dlgknwe fijf fseiof js sdk");
		ChatMessage msg4 = new ChatMessage(null, "hsha såpegi åoepsifos sdfp osdfk");
		ChatMessage msg5 = new ChatMessage(null, "hsdggdr drg dg dsgrdg dg drgdr gdrg drgdrgdrgdrgdrgdiogjdfpogidr gd drg");
		
		try {
			graphics.renderTexts(new Text[0]);
			Thread.sleep(1000);
			container.addMessage(msg1);
			System.out.println("msg1 height: " + msg1.getHeight());
			System.out.println("msg1 lines: " + msg1.getLineCount());
			graphics.renderTexts(container.getTexts());
			Thread.sleep(1000);
			container.addMessage(msg2);
			System.out.println("msg2 height: " + msg2.getHeight());
			System.out.println("msg2 lines: " + msg2.getLineCount());
			graphics.renderTexts(container.getTexts());
			Thread.sleep(1000);
			container.addMessage(msg3);
			System.out.println("msg3 height: " + msg3.getHeight());
			System.out.println("msg3 lines: " + msg3.getLineCount());
			graphics.renderTexts(container.getTexts());
			Thread.sleep(1000);
			container.addMessage(msg4);
			graphics.renderTexts(container.getTexts());
			Thread.sleep(1000);
			container.addMessage(msg5);
			graphics.renderTexts(container.getTexts());
			Thread.sleep(60000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
		
		return true;
	}
}
