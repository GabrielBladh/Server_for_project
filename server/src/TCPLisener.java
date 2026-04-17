import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPLisener implements Runnable {
    Controller controller;

    public TCPLisener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        int port = 9500;
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("TCPLisener is listening on port " + port);

        while (true) {
            System.out.println("Väntar på ny Client");
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Accepted connection from " + socket.getInetAddress().getHostName());

                Thread thread = new Thread(new ClientHandler(socket, controller));
                thread.start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
