package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer_02 {

    private static final int PORT = 2002;
    private static final int MONITOR_PORT = 2000;
    private static final int PORT_SERVER_01 = 2001;
    private static final int PORT_SERVER_03 = 2003;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Socket monitorSocket;
    private static PrintWriter outToMonitor;
    private static ServerConnector server01Connector;
    private static ServerConnector server03Connector;
    private static Boolean connected = false;

    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(PORT)) {
            monitorSocket = new Socket("localhost", MONITOR_PORT);
            outToMonitor = new PrintWriter(monitorSocket.getOutputStream(), true);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

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

    public static class ClientHandler implements Runnable {

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
//                broadcast(userName + " ket noi");
                name = userName;
                if (!connected) {
                    server01Connector = new ServerConnector("localhost", PORT_SERVER_01, "Server 02");
                    server03Connector = new ServerConnector("localhost", PORT_SERVER_03, "Server 02");
                    connected = true;
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("bye".equals(inputLine)) {
                        break;
                    }
                    String Check_address = inputLine.substring(0, inputLine.lastIndexOf("."));
                    switch (Check_address) {
                        case "193.24.56":
                            System.out.println("Chuyen den Router 3");
                            server03Connector.sendMessage(inputLine);
                            break;
                        case "200.100.10":
                            broadcast(inputLine, false);
                            System.out.println("Chuyen den Host D");
                            break;
                        case "196.192.6":
                        case "195.192.150":
                            System.out.println("Chuyen den Router 1");
                            server01Connector.sendMessage(inputLine);
                            break;
                        default:
                            System.out.println("Nhap Sai Dia Chi");
                            break;
                    }
                    notifyMonitor("Thong diep da den Server 2", outToMonitor);
                }

                String left = userName + " has left the chat.";
                notifyMonitor(left, outToMonitor);
                broadcast(userName + " has left the chat.", true);
                clients.remove(this);

            } catch (IOException e) {
            }
        }

        private void broadcast(String message, Boolean out) {
            if (!out) {
                for (ClientHandler client : clients) {
                    if (client.name.equals("200.100.10.0")) {
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
            ChatServer_02.notifyMonitor(message, outToMonitor);
        }
    }
}
