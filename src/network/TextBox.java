package network;

import java.util.Scanner;

import graphics.Font;
import graphics.Text;


/**
 * does not support newLine, and character width is a fixed value.
 * @author Eirik
 *
 */
public class TextBox {

	
	private Text outputText;
	private StringBuilder string;
	private String originalString = "";
	
	private float x, y, width, height;
	private int fontSize;
	private Font font;
	
	private int charsPerLine;
	
	private int linesCount;
	private int charsInLastLine;
	
	
	public TextBox(float x, float y, float width, Font font, int fontSize) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.font = font;
		this.fontSize = fontSize;
		height = 0;
		linesCount = 0;
		charsInLastLine = 0;
		string = new StringBuilder();
		outputText = new Text("", font, fontSize, x, y, 0);
		
		calculateCharsPerLine();
	}
	
	//dependent on width and fontSize.
	private void calculateCharsPerLine() {
		float charWidth = fontSize*0.5f;
		charsPerLine = (int)(width/charWidth);
	}
	private void calculateHeight() {
		height = fontSize*linesCount;
	}
	
	public void setText(String text) {
		string.setLength(0); //clear
		linesCount = 0;
		charsInLastLine = 0;
		addFitText(text);
		originalString = text;
	}
	
	public void addText(String text) {
		addFitText(text);
		originalString += text;
	}
	

	/**
	 * Dependent on width.
	 * fits text to width, sets new height, and sets "string" to new text.
	 * Does not support new lines. The are removed
	 * @param text
	 */
	private void addFitText(String text) {
		//int textLength = text.length();
		int lineCharsLeft = charsPerLine - charsInLastLine;
		System.out.println("Text: " + text);
		if (charsInLastLine == 0 && text.length() > 0 ) linesCount++; //eeeehh
		Scanner scanner = new Scanner(text); //default deliminator: " "
		scanner.useDelimiter("\\s|\\n");
		String word;
		int wordLen;
		while (scanner.hasNext()) {
			word = scanner.next();
			wordLen = word.length();
			
			if (lineCharsLeft >= wordLen) {
				string.append(word);
				string.append(' ');
				lineCharsLeft -= (wordLen+1);
			}
			else {
				string.append("\n");
				string.append(word);
				string.append(' ');
				lineCharsLeft = charsPerLine - wordLen;
				linesCount++;
			}
		}
		charsInLastLine = charsPerLine - lineCharsLeft;
		scanner.close();
		
		calculateHeight();
		
		outputText.setString(string.toString());

	}
	
	public Text getGraphicsText() {
		return outputText;
	}
	
	public String getString() {
		return string.toString();
	}
	
	public void setWidth(float width) {
		this.width = width;
		calculateCharsPerLine();
		addFitText(originalString);
	}
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public int getLineCount() {
		return this.linesCount;
	}
	
	public void setX(float x) {
		this.x = x;
		outputText.setX(x);
	}
	public void setY(float y) {
		this.y = y;
		outputText.setY(y);
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
}
