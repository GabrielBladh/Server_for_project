package tictactoe;

import Game.Game;

public class TicTacToe implements Game {

    String[][] board = new String[3][3];
    String[][] boardBlink = new String[3][3];
    String currentPlayer = "B";
    boolean isGameEnded = false;
    int[][][] buttons = {
            { {9,10,17,18}, {11,12,19,20}, {13,14,21,22} },
            { {25,26,33,34}, {27,28,35,36},{29,30,37,38} },
            { {41,42,49,50}, {43,44,51,52}, {45,46,53,54} }
    };
    boolean AI = false;

    public TicTacToe() {
        boardBlink = new String[][]{
                {"0","0","0"},
                {"0","0","0"},
                {"0","0","0"}
        };
    }

    public void setAI(boolean AI) {
        this.AI = AI;
    }

    @Override
    public String getGameStatus() {
        char[] boardStatus = new char[64];

        for (int i = 0; i < boardStatus.length; i++) {
            boardStatus[i] = 'N';
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] != null) {
                    for (int button : buttons[row][col]) {
                        boardStatus[button] = board[row][col].charAt(0);
                    }
                }
            }
        }

        return new String(boardStatus);
    }

    public String getTurn() {
        return currentPlayer;
    }


    @Override
    public boolean placeTile(int row, int col) {
        if (isGameEnded) return false;

        boolean found = false;
        int value = row * 8 + col;

        for (int row1 = 0; row1 <3 ; row1++) {
            for (int col1 = 0; col1 < 3 ; col1++) {
                for (int b : buttons[row1][col1]) {
                    if (b == value) {
                        found = true;
                    }
                }
                if  (found){
                    board[row1][col1] = currentPlayer;
                    found = false;
                }
            }
        }

        checkEndGame();
        if (!isGameEnded) {
            endTurn();
        }

        return true;
    }

    public void endTurn() {
        currentPlayer = currentPlayer.equals("B") ? "R" : "B";
    }

    @Override
    public String getGameEnd() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                sb.append(boardBlink[row][col].equals("1") ? "1" : "0");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isGameEnded() {
        return isGameEnded;
    }

    @Override
    public String getBoardStatus() {
        return "";
    }

    public void checkEndGame() {

        // rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] != null &&
                    board[row][0].equals(board[row][1]) &&
                    board[row][1].equals(board[row][2])) {

                isGameEnded = true;
                for (int col = 0; col < 3; col++) {
                    boardBlink[row][col] = "1";
                }
            }
        }

        // columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] != null &&
                    board[0][col].equals(board[1][col]) &&
                    board[1][col].equals(board[2][col])) {

                isGameEnded = true;
                for (int row = 0; row < 3; row++) {
                    boardBlink[row][col] = "1";
                }
            }
        }

        // diagonals
        if (board[0][0] != null &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2])) {

            isGameEnded = true;
            boardBlink[0][0] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][2] = "1";
        }

        if (board[0][2] != null &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {

            isGameEnded = true;
            boardBlink[0][2] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][0] = "1";
        }
    }
}