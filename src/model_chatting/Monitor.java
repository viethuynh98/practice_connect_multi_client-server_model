package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Monitor {

    private static final int MONITOR_PORT = 2000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(MONITOR_PORT)) {
            System.out.println("Monitor is running on port " + MONITOR_PORT);

            while (true) {
                Socket monitorSocket = serverSocket.accept();
                System.out.println("Monitor connected");

                new Thread(() -> {
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()));
//                        PrintWriter out = new PrintWriter(monitorSocket.getOutputStream(), true)
                    ) {
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println("Monitor received: " + message);
                        }
                    } catch (IOException e) {
                    }
                }).start();
            }
        } catch (IOException e) {
        }
    }
}
