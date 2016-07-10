package network;

import java.net.Socket;

/**
 * Created by Thomas on 01.07.2016.
 */
public class Task {

    String message = "";
    Socket source;

    public Task(String message, Socket source){
        this.message = message;
        this.source = source;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSource(Socket source) {
        this.source = source;
    }

    public Socket getSource() {
        return source;
    }
}

