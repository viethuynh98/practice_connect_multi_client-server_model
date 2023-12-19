package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient_01 {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2001;
    private static final int MONITOR_PORT = 2000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            Socket monitorSocket = new Socket("localhost", MONITOR_PORT);
            PrintWriter monitorOut = new PrintWriter(monitorSocket.getOutputStream(), true);
            System.out.println("Connected to the chat server");

//            System.out.print("Enter your address: ");
            System.out.print("Welcome!!!");
//            String userName = consoleReader.readLine();
            String userName = "195.192.150.10";
            out.println(userName);

            System.out.println("Bat dau trao doi thong tin");
            new Thread(() -> {
                try {
                    String serverResponse;
                    while (!socket.isClosed() && (serverResponse = in.readLine()) != null) {
                        if (serverResponse.equalsIgnoreCase("false")) {
                            monitorOut.println("Nhap sai dia chi");
                            System.out.println("Nhap sai dia chi");
                        } else {
                            // 195.192.150.10-200.100.10.13*alo alo alo
                            String address = serverResponse.substring(0, serverResponse.indexOf("-", 1)); // 195.192.150.10
                            if (address.equalsIgnoreCase(userName)) {
                                monitorOut.println("Nhap trung dia chi");
                                System.out.println("Nhap trung dia chi");
                            } else {
                                address = Address.checkAddress(address, userName); // 195.192.150.10
                                String content = serverResponse.substring(serverResponse.indexOf("*", 1) + 1, serverResponse.length());
                                monitorOut.println(address + ": " + content);
//                        monitorOut.println("Da nhan:" + serverResponse);
                                System.out.println(address + ": " + content);
                            }
                        }
                    }
                } catch (IOException e) {
                }
            }).start();

            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                if (!socket.isClosed()) {
                    userInput = userInput.trim(); // alo alo alo
                    String add = userInput.substring(0, userInput.indexOf("*", 1));
                    add = add.trim();
                    add = Address.checkAddress(userName, add);
                    String content = userInput.substring(userInput.indexOf("*", 1) + 1, userInput.length());
                    monitorOut.println(add + ": " + content);
                    String sentMsg = userName + "-" + userInput; // 195.192.150.10-200.100.10.13*alo alo alo
                    out.println(sentMsg);
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
