package model_chatting;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnector {
    private Socket socket;
    private PrintWriter out;

    public ServerConnector(String serverAddress, int serverPort, String str) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            out.println(str); // gửi tin nhắn tới Server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}



