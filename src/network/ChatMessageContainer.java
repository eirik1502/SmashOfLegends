package network;

import java.util.ArrayList;
import java.util.LinkedList;

import rooms.Text;

public class ChatMessageContainer {

	
	private LinkedList<ChatMessage> messages = new LinkedList<>();
	private float x, y, width, height;
	//private float messageHeight = 30f;
	
	public ChatMessageContainer(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void addMessage( ChatMessage message) {
		message.enterContainer(x, y, width);
		for (ChatMessage m : messages) {
			m.addY(message.getHeight());
		}
		messages.add(message);
		
		//check if oldest message needs to be removed
		ChatMessage oldestMessage = messages.peek();
		System.out.println("oldest message y: " +oldestMessage.getY());
		System.out.println("oldest message height: " +oldestMessage.getHeight());
		if (oldestMessage.getY()+oldestMessage.getHeight() > y+height) {
			System.out.println("Should remove");
			removeOldestMessage();
		}
	}
	public Text[] getTexts() {
		int messagesSize = messages.size();
		Text[] texts = new Text[messagesSize];
		for (int i = 0; i < messagesSize; i++) {
			texts[i] = messages.get(i).getText();
		}
		return texts;
	}
	
	private void removeOldestMessage() {
		messages.poll();
	}
	
}
