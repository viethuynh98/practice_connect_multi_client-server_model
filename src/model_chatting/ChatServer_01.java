package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer_01 {

    private static final int PORT = 2001;
    private static final int MONITOR_PORT = 2000;
    private static final int PORT_SERVER_02 = 2002;
    private static final int PORT_SERVER_04 = 2004;
    private static List<ClientHandler> clients = new ArrayList<>(); // danh sách quản lý các Socket kết nối tới Server
    private static Socket monitorSocket;
    private static PrintWriter outToMonitor;
    private static BufferedReader getFromMonitor;
    private static ServerConnector server02Connector;
    private static ServerConnector server04Connector;

    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(PORT)) { // tạo Serversocket nhận các kết nối từ các Client - server khác
            monitorSocket = new Socket("localhost", MONITOR_PORT);
            outToMonitor = new PrintWriter(monitorSocket.getOutputStream(), true);
            getFromMonitor = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()));
            System.out.println("Server is running on port " + PORT);
            String monitorMessage;
            // chờ tin nhắn thông báo hoạt động từ Monitor
            while ((monitorMessage = getFromMonitor.readLine()) != null) {
                if (monitorMessage.equalsIgnoreCase("Connected")) {
                    server02Connector = new ServerConnector("localhost", PORT_SERVER_02, "Server 01");
                    server04Connector = new ServerConnector("localhost", PORT_SERVER_04, "Server 01");
                    break;
                }
            }
            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
        }
    }

    private static void notifyMonitor(String message, PrintWriter monitorOut) {
        monitorOut.println(message);
    }

    public static class ClientHandler implements Runnable { // thread

        private Socket clientSocket;
        private List<ClientHandler> clients;
        private String name = "";
        private PrintWriter out;

        public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
            this.clientSocket = clientSocket;
            this.clients = clients;

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String userName = in.readLine();
                System.out.println(userName + " ket noi");
                name = userName;
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("bye".equals(inputLine)) {
                        break;
                    }
                    // 195.192.150.10-200.100.10.13*alo alo alo
                    String Check_address = inputLine.substring(0, inputLine.indexOf("*")); // 195.192.150.10-200.100.10.13
                    Check_address = Check_address.substring(Check_address.indexOf("-", 1) + 1, Check_address.lastIndexOf(".")); // 200.100.10
                    notifyMonitor("Thong diep da den Server 1", outToMonitor);
                    switch (Check_address) { // 200.100.10
                        case "196.192.6":
                            System.out.println("Chuyen den Router 4");
                            server04Connector.sendMessage(inputLine); //  // 195.192.150.10-200.100.10.13*alo alo alo
                            break;
                        case "195.192.150":
                            broadcast(inputLine, false);
                            System.out.println("Chuyen den Host A"); //  // 195.192.150.10-200.100.10.13*alo alo alo
                            break;
                        case "200.100.10":
                        case "193.24.56":
                            System.out.println("Chuyen den Router 2");
                            server02Connector.sendMessage(inputLine);
                            break;
                        default:
                            String returnStr = "false";
                            broadcast(returnStr, false);
                            System.out.println("Nhap Sai Dia Chi");
                            break;
                    }
                }

                String left = userName + " has left the chat.";
                notifyMonitor(left, outToMonitor);
                broadcast(userName + " has left the chat.", true);
                clients.remove(this); // loại bỏ Client khỏi danh sách quản lý.

            } catch (IOException e) {
            }
        }

        private void broadcast(String message, Boolean out) {
            if (!out) {
                for (ClientHandler client : clients) {
                    if (client.name.equals("195.192.150.10")) {
                        client.sendMessage(message);
                    }
                }
            } else {
                for (ClientHandler client : clients) {
                    if (client != this) {
                        client.sendMessage(message);
                    }
                }
            }

        }

        private void sendMessage(String message) {
            out.println(message);
        }

        private void notifyMonitor(String message, PrintWriter outToMonitor) {
            ChatServer_01.notifyMonitor(message, outToMonitor);
        }
    }
}
