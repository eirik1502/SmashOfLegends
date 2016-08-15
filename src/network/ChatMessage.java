package network;

import graphics.Font;
import graphics.Text;
import network.clientLobby.ConnectedClientImage;

public class ChatMessage {

	
	private final TextBox textBox;
	
	private float x, y, width, height;
	private float marginLeft = 10f, marginRight = 10f, marginTop = 10f, marginBottom = 10f;
	
	private ConnectedClientImage ownerClient;
	private String startString;
	
	
	public ChatMessage(ConnectedClientImage ownerClient, String text) {
		startString = text;
		this.ownerClient = ownerClient;
		this.textBox = new TextBox( 0f, 0f, 0f, Font.getStandardFont(), 18);
	}
	
	public void enterContainer(float x, float y, float width) {
		setX(x);
		setY(y);
		setWidth(width);
		
		textBox.setText(startString);
		
		this.height = this.getMsgHeightFromTextBox();

	}
	
	public String getString() {
		return textBox.getString();
	}
	
	public Text getText() {
		return textBox.getGraphicsText();
	}
	public ConnectedClientImage getOwnerClient() {
		return ownerClient;
	}
	
	public int getLineCount() {
		return textBox.getLineCount();
	}
	public float getHeight() {
		return height;
	}
	public float getWidth() {
		return width;
	}
	private void setWidth(float width) {
		this.width = width;
		setTextBoxWidth(width);
	}
	
	private void setTextBoxX(float msgX) {
		textBox.setX(x+marginLeft);
	}
	private void setTextBoxY(float msgY) {
		textBox.setY(y+marginTop);
	}
	private void setTextBoxWidth(float msgWidth) {
		textBox.setWidth(width- (marginLeft+marginRight));
	}
	private float getMsgHeightFromTextBox() {
		return textBox.getHeight()+marginTop+marginBottom;
	}
	
	public void setX( float x) {
		this.x = x;
		setTextBoxX(x);
	}
	public void addX( float x) {
		setX( getX()+x );
	}
	public void setY( float y) {
		this.y = y;
		setTextBoxY(y);
	}
	public void addY( float y) {
		setY( getY()+y );
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
}
