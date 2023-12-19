package model_chatting;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Monitor_Temp {

    private static final int MONITOR_PORT = 2000;
    private static final int NUM_SERVERS = 4;
    private static int connectedServers = 0;

    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(MONITOR_PORT)) {
            System.out.println("Monitor is running on port " + MONITOR_PORT);

            // Mảng để lưu trữ các kết nối từ các server
            Socket[] serverSockets = new Socket[NUM_SERVERS];

            // Chờ kết nối từ các server
            while (connectedServers < NUM_SERVERS) {
                Socket monitorSocket = serverSocket.accept();
                System.out.println("Monitor connected to Server " + (connectedServers + 1));

                // Lưu trữ kết nối trong mảng
                serverSockets[connectedServers] = monitorSocket;

                // Tăng số lượng server đã kết nối
                connectedServers++;

                // Bắt đầu một luồng mới để xử lý kết nối từ server
                new Thread(() -> {
                    try ( BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()))) {
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println("Monitor received: " + message);
                        }
                    } catch (IOException e) {
                    }
                }).start();
            }
            // Khi đã kết nối đủ số lượng server, gửi thông báo xác nhận cho các server
            for (int i = 0; i < NUM_SERVERS; i++) {
                try {
                    serverSockets[i].getOutputStream().write("Connected to Monitor".getBytes());
                } catch (IOException e) {
                }
            }
            while (true) {
                Socket monitorSocket = serverSocket.accept();
                System.out.println("Monitor connected");

                new Thread(() -> {
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()));
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
