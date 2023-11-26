package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server_temp {

    private static final int PORT = 2001;
    private static final int MONITOR_PORT = 3001;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Socket monitorSocket;
    private static PrintWriter outToMonitor;

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
//        System.out.println(message);
        monitorOut.println(message);
    }

    public static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private List<ClientHandler> clients;
        private String name = "";
//        private Socket monitorSocket;
        private PrintWriter out;
//        private PrintWriter outToMonitor;

        public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
            this.clientSocket = clientSocket;
            this.clients = clients;
//            try {
//                this.monitorSocket = new Socket("localhost", MONITOR_PORT);
//                
////                notifyMonitor("New client connected", monitorSocket);
//            } catch (IOException e) {
//            }
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                outToMonitor = new PrintWriter(this.monitorSocket.getOutputStream(), true);
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String userName = in.readLine();
                broadcast(userName + " has joined the chat.");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("bye".equals(inputLine)) {
                        break;
                    }
                    notifyMonitor("Thong diep da den Server 1", outToMonitor);
                    broadcast(userName + ": " + inputLine);
                }

                String left = userName + " has left the chat.";
                notifyMonitor(left, outToMonitor);
                broadcast(userName + " has left the chat.");
                clients.remove(this);

            } catch (IOException e) {
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(message);
                }
            }
        }

        private void sendMessage(String message) {
            out.println(message);
        }

        private void notifyMonitor(String message, PrintWriter outToMonitor) {
            Server_temp.notifyMonitor(message, outToMonitor);
        }
    }
}
