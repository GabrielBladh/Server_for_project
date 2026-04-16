import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class Main
{
    public static void main(String[] args) throws IOException {
        //port som kommunikation sker på
        int port = 8000;
        //socket är end-point för kommunikation mellan två enheter.
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("System körs på port: " + port);
        while (true){
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);
            PrintWriter out = new PrintWriter(outputStream, true);

            //incomingData är data som skickas från både IS/klient till socket
            String incomingData = in.readLine();

        }
    }
}
