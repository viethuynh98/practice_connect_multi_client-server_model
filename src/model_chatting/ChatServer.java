package model_chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static final int PORT = 2001;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private List<ClientHandler> clients;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
            this.clientSocket = clientSocket;
            this.clients = clients;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
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
                    broadcast(userName + ": " + inputLine);
                }

                clients.remove(this);
                broadcast(userName + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
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
    }
}
