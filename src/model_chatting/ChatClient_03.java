package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient_03 {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2003;
    private static final int MONITOR_PORT = 2000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            Socket monitorSocket = new Socket("localhost", MONITOR_PORT);
            PrintWriter monitorOut = new PrintWriter(monitorSocket.getOutputStream(), true)
        ) {
            System.out.println("Connected to the chat server");

            System.out.print("Enter your name: ");
            String userName = consoleReader.readLine();
            out.println(userName);

            new Thread(() -> {
                try {
                    String serverResponse;
                    while (!socket.isClosed() && (serverResponse = in.readLine()) != null) {
                        monitorOut.println(userName + " Da nhan duoc thong diep tu " + serverResponse);
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                }
            }).start();

            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                if (!socket.isClosed()) {
                    monitorOut.println(userName + ": " + userInput);
                    out.println(userInput);
                    if ("bye".equals(userInput)) {
                        break;
                    }   
                }
            }
            socket.close();
        } catch (IOException e) {
        }
    }
}

