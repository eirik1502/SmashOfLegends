package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class LogWriter {
	
	
    private PrintWriter logWriter;
	
    
	public LogWriter( String filepath) {
		try {
			logWriter = new PrintWriter(filepath);
			
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not create log file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void flush() {
		logWriter.flush();
	}
    public void print(String s) {
    	this.logWriter.print(s);
    }
    public void println(String s) {
    	this.logWriter.println(s);
    }
    public void errPrint(String s) {
    	String errorMsg = "---ERROR--- ";
    	print(errorMsg+s);
    }
    public void errPrintln(String s) {
    	errPrint(s+"\n");
    }
    public void printException(Exception e) {
    	this.logWriter.println(e.getStackTrace());
    }
    public void printlnSucsess(String s) {
    	String sucsess = "SUCSESS";
    	println(sucsess + s);
    }
    public void printlnSucsess() {
    	printlnSucsess("");
    }
}
