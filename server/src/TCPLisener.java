
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPLisener implements Runnable {
    Controller controller;

    // Skapar en pool som återanvänder max 20 trådar istället för att skapa tusentals nya
    private final ExecutorService threadPool = Executors.newFixedThreadPool(20);

    public TCPLisener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        int port = 9000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCPLisener is listening on port " + port);

            while (true) {
                System.out.println("Väntar på ny Client");
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Accepted connection from " + socket.getInetAddress().getHostName());

                    // Lånar en befintlig tråd från poolen istället för 'new Thread()'
                    threadPool.execute(new ClientHandler(socket, controller));

                } catch (Exception e) {
                    System.out.println("Fel vid anslutning: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Serverfel: " + e.getMessage());
        }
    }
}