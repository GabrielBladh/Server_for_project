import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import Game.Game;
import checkers.Checkers;
import tictactoe.TicTacToe;

public class ClientHandler implements Runnable {
    Socket socket;
    Controller controller;

    public ClientHandler(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;


    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            String command = in.readLine();
            System.out.println("Request: " + command);

            if (command.equals("Checkers")) {
                controller.setGame(new Checkers());
                System.out.println("Response: Checkers started");
                out.println("StartGame");
                out.flush();
                socket.close();
            }
            else if (command.equals("Tic Tac Toe")) {
                controller.setGame(new TicTacToe());
                System.out.println("Response: Tic Tac Toe : started");
                out.println("StartGame");
                out.flush();
                socket.close();
            }
            else if (command.equals("update")) {
                String response = controller.getGame().getGameStatus();
                System.out.println("Response: " + response);
                out.println(response);
                out.flush();
                socket.close();
            }
            else if (command.equals("update_blink")) {
                String response = controller.getGame().getGameEnd();
                System.out.println("Response: " + response);
                out.println(response);
                out.flush();
                socket.close();
            }

            else if (command.equals("update_chess")){
                String response = controller.getGame().getBoardStatus();
                System.out.println("Response: " + response);
                out.println(response);
                out.flush();
                socket.close();
            }
            else if (command.equals("turn")) {
                String response = controller.getGame().getTurn();
                out.println(response);
                out.flush();
                socket.close();
            }
            else{
                System.out.println("Response: " + command);
                String[] commands = command.split(":");
                controller.getGame().placeTile(Integer.parseInt(commands[0]), Integer.parseInt(commands[1]));
                out.println(commands[0] + ":" + commands[1]);
                out.flush();
                socket.close();

            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
