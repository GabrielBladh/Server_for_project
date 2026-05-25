import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import Game.Game;
import U4.GameModel;
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
                if (!socket.isConnected()){
                    socket.close();
                }
                String command = in.readLine();
                if (command == null){
                    System.out.println("Disconnected connection from " + socket.getInetAddress().getHostName());
                    socket.close();
                    break;
                }

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
                } else if (command.equals("U4")) {
                    controller.setGame(new GameModel()); // Startar Omvälvning!
                    System.out.println("U4 started");
                }
                else if (command.equals("Chess"))
                {
                    controller.setGame(new Chess());
                    System.out.println("Chess started");
                } else if (command.equals("Chess AI")) {
                    Chess aiGame = new Chess();
                    aiGame.setAI(true);
                    aiGame.setDifficultyLevel("easy");
                    controller.setGame(aiGame);
                    System.out.println("Tic Tac Toe AI started");
                }  else if (command.equals("update")) {
                    if (controller.getGame() == null){
                        out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
                        out.flush();
                        continue;
                    }
                    String response = controller.getGame().getGameStatus();
                    out.println(response);
                    out.flush();

                }
                else if (command.equals("update_blink")) {
                    if (controller.getGame() == null){
                        out.println("0000000000000000000000000000000000000000000000000000000000000000");
                        out.flush();
                        continue;
                    }
                    String response = controller.getGame().getGameEnd();
                    out.println(response);
                    out.flush();

                }

                else if (command.equals("update_chess")){
                    if (controller.getGame() == null){
                        out.println("0000000000000000000000000000000000000000000000000000000000000000");
                        out.flush();
                        continue;
                    }
                    String response = controller.getGame().getBoardStatus();
                    out.println(response);
                    out.flush();
                }
                else if (command.equals("turn")) {
                    if (controller.getGame() == null){
                        System.out.println("R");
                        out.println("R");
                        out.flush();
                        continue;
                    }
                    String response = controller.getGame().getTurn();
                    out.println(response);
                    out.flush();
                }
                else if (command.equals("press")) {
                    command = in.readLine(); // Läser förhoppningsvis "3:4"
                    if (command == null || !command.contains(":")) {
                        System.out.println("TCP Krock undviken! Förväntade koordinater, fick: " + command);
                        continue;
                    }

                    try {
                        String[] commands = command.split(":");
                        int x_värde = Integer.parseInt(commands[0].trim());
                        int y_värde = Integer.parseInt(commands[1].trim());

                        Game currentGame = controller.getGame();

                        if (currentGame instanceof Checkers) {
                            if (((Checkers) currentGame).isAITurn()) {
                                continue;
                            }
                        } else if (currentGame instanceof TicTacToe) {
                            if (((TicTacToe) currentGame).isAITurn()) {
                                continue;
                            }
                        }

                        currentGame.placeTile(x_värde, y_värde);

                        System.out.println("Button pressed: " + x_värde + ":" + y_värde);

                    } catch (NumberFormatException e) {
                        System.out.println("Kunde inte läsa siffrorna: " + command);
                    }
                }            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
