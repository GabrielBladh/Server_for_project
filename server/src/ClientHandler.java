import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import checkers.Checkers;
import chess.Chess;
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

            while (true){
                String command = in.readLine();
                if (command == null){
                    System.out.println("Disconnected connection from " + socket.getInetAddress().getHostName());
                    socket.close();
                    break;
                }
                System.out.println("Request: " + command);

                if (command.equals("Checkers")) {
                    controller.setGame(new Checkers());
                    System.out.println("Checkers started");
                }
                else if (command.equals("Checkers AI")) {
                    Checkers aiGame = new Checkers();
                    aiGame.setAI(true);
                    controller.setGame(aiGame);
                    System.out.println("Checkers AI started");
                }

                else if (command.equals("Tic Tac Toe")) {
                    controller.setGame(new TicTacToe());
                    System.out.println("Tic Tac Toe started");
                }
                else if (command.equals("Tic Tac Toe AI")) {
                    TicTacToe aiGame = new TicTacToe();
                    aiGame.setAI(true);
                    controller.setGame(aiGame);
                    System.out.println("Tic Tac Toe AI started");
                }
                else if (command.equals("Chess"))
                {
                    controller.setGame(new Chess());
                    System.out.println("Chess started");
                }
                else if (command.equals("update")) {
                    if (controller.getGame() == null){
                        System.out.println("Game is null");
                        out.println("noGame");
                        out.flush();
                        continue;
                    }
                    String response = controller.getGame().getGameStatus();
                    System.out.println("Response: " + response);
                    out.println(response);
                    out.flush();

                }
                else if (command.equals("update_blink")) {
                    String response = controller.getGame().getGameEnd();
                    System.out.println("Response: " + response);
                    out.println(response);
                    out.flush();

                }

                else if (command.equals("update_chess")){
                    String response = controller.getGame().getBoardStatus();
                    System.out.println("Response: " + response);
                    out.println(response);
                    out.flush();
                }
                else if (command.equals("turn")) {
                    String response = controller.getGame().getTurn();
                    System.out.println("Response: " + response);
                    out.println(response);
                    out.flush();
                }
                else if (command.equals("press")) {
                    command = in.readLine();
                    String[] commands = command.split(":");
                    int x_värde = Integer.parseInt(commands[0]);
                    int y_värde = Integer.parseInt(commands[1]);
                    controller.getGame().placeTile(x_värde, y_värde);
                    System.out.println("Button pressed: " + x_värde + ":" + y_värde);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
