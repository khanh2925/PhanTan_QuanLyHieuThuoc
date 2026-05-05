package network;

import db.DataSeeder;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9090;
    private static final int THREAD_POOL_SIZE = 20;

    public static void main(String[] args) {
        DataSeeder.seed();
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== Quản Lý Hiệu Thuốc - Server ===");
            System.out.println("Server đang chạy tại cổng " + PORT);
            System.out.println("Sẵn sàng nhận kết nối...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[+] Client kết nối: " + socket.getInetAddress().getHostAddress());
                pool.submit(new ClientHandler(socket));
            }
        } catch (Exception e) {
            System.err.println("Server lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
