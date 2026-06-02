package tictactoe;

import Game.Game;

import java.io.IOException;

public class TicTacToe implements Game {

    String[][] board = new String[3][3];
    String[][] boardBlink = new String[3][3];

    String currentPlayer = "B";

    boolean isGameEnded = false;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean aiGame = false;

    int[][][] buttons = {
            { {0,1,8,9},       {3,4,11,12},     {6,7,14,15} },
            { {24,25,32,33},   {27,28,35,36},   {30,31,38,39} },
            { {48,49,56,57},   {51,52,59,60},   {54,55,62,63} }
    };

    public TicTacToe() {

        boardBlink = new String[][]{
                {"0","0","0"},
                {"0","0","0"},
                {"0","0","0"}
        };
    }

    @Override
    public String getGameStatus() {
        char[] boardStatus = new char[64];

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int index = r * 8 + c;

                if (r == 2 || r == 5 || c == 2 || c == 5) {
                    boardStatus[index] = 'G'; // Rita grönt nät
                } else {
                    boardStatus[index] = 'N'; // Tom ruta
                }
            }
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

        int value = row * 8 + col;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                for (int b : buttons[r][c]) {

                    if (b == value) {

                        // FAS 1 - lägga ut pjäser
                        if (!isAllPiecesPlaced()) {

                            if (board[r][c] != null) {
                                return false;
                            }

                            board[r][c] = currentPlayer;

                            checkEndGame();

                            if (!isGameEnded) {
                                endTurn();
                            }

                            return true;
                        }

                        // FAS 2 - flytta pjäser
                        return movePiece(r, c);
                    }
                }
            }
        }

        return false;
    }

    private boolean movePiece(int row, int col) {

        if (selectedRow == -1) {
            if (board[row][col] != null && board[row][col].equals(currentPlayer)) {
                selectedRow = row;
                selectedCol = col;
                //System.out.println(currentPlayer + " markerade pjäs på: " + row + "," + col);
                return true;
            }
            return false;
        }


        if (row == selectedRow && col == selectedCol) {
            selectedRow = -1;
            selectedCol = -1;
            //System.out.println("Pjäsen sattes ner igen (avmarkerad).");
            return true;
        }

        if (board[row][col] != null && board[row][col].equals(currentPlayer)) {
            selectedRow = row;
            selectedCol = col;
            //System.out.println("Bytt markering till ny pjäs på: " + row + "," + col);
            return true;
        }

        if (board[row][col] == null) {

            if (isAdjacent(selectedRow, selectedCol, row, col)) {

                board[row][col] = currentPlayer; // Flytta pjäsen
                board[selectedRow][selectedCol] = null; // Ta bort från gamla rutan

                selectedRow = -1;
                selectedCol = -1;

                checkEndGame();

                if (!isGameEnded) {
                    endTurn();
                }
                return true;
            } else {
                //System.out.println("Ogiltigt drag! Rutan är för långt bort. Pjäsen avmarkeras.");
                selectedRow = -1;
                selectedCol = -1;
                return false;
            }
        }

      //  System.out.println("Klickade på motståndaren. Pjäsen avmarkeras.");
        selectedRow = -1;
        selectedCol = -1;
        return false;
    }
    private boolean isAdjacent(int fromRow, int fromCol, int toRow, int toCol) {

        return Math.abs(fromRow - toRow) <= 1 &&
                Math.abs(fromCol - toCol) <= 1;
    }

    private boolean isAllPiecesPlaced() {

        int blueCounter = 0;
        int redCounter = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                if ("B".equals(board[row][col])) {
                    blueCounter++;
                }

                else if ("R".equals(board[row][col])) {
                    redCounter++;
                }
            }
        }

        return blueCounter == 3 && redCounter == 3;
    }

    public void placeTileAI(int fromRow, int fromCol, int toRow, int toCol) {

        if (isGameEnded) return;

        // FAS 1
        if (!isAllPiecesPlaced()) {

            if (board[toRow][toCol] == null) {

                board[toRow][toCol] = currentPlayer;

                checkEndGame();

                if (!isGameEnded) {
                    endTurn();
                }
            }

            return;
        }

        // FAS 2
        if (board[fromRow][fromCol] != null &&
                board[fromRow][fromCol].equals(currentPlayer) &&
                board[toRow][toCol] == null &&
                isAdjacent(fromRow, fromCol, toRow, toCol)) {

            board[toRow][toCol] = currentPlayer;

            board[fromRow][fromCol] = null;

            checkEndGame();

            if (!isGameEnded) {
                endTurn();
            }
        }
    }

    public void endTurn() {

        currentPlayer = currentPlayer.equals("B") ? "R" : "B";

        if (aiGame &&
                currentPlayer.equals("R") &&
                !isGameEnded) {

            doComputerMove();
        }
    }

    @Override
    public String getGameEnd() {
        char[] blinkStatus = new char[64];

        for (int i = 0; i < 64; i++) {
            blinkStatus[i] = '0';
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                if ("1".equals(boardBlink[row][col])) {
                    for (int button : buttons[row][col]) {
                        blinkStatus[button] = '1';
                    }
                }
            }
        }

        return new String(blinkStatus);
    }

    @Override
    public boolean isGameEnded() {
        return isGameEnded;
    }

    @Override
    public String getBoardStatus() {
        return "";
    }

    public void saveGame(Game game) throws IOException {

    }

    @Override
    public void setGameStatus(String gameStatus) {

    }

    @Override
    public void setBoardStatus(String boardStatus) {

    }

    @Override
    public void setTurn(String turn) {

    }

    @Override
    public void setGameEnd(String gameEnd) {

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

        // diagonal 1
        if (board[0][0] != null &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2])) {

            isGameEnded = true;

            boardBlink[0][0] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][2] = "1";
        }

        // diagonal 2
        if (board[0][2] != null &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {

            isGameEnded = true;

            boardBlink[0][2] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][0] = "1";
        }
    }

    public void setAI(boolean aiGame) {
        this.aiGame = aiGame;
    }

    public boolean isAITurn() {
        return aiGame && currentPlayer.equals("R");
    }

    private void doComputerMove() {

        new Thread(() -> {

            try {
                Thread.sleep(1000);
            }

            catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            if (isGameEnded || !currentPlayer.equals("R")) {
                return;
            }

            // FAS 1 - placera pjäser
            if (!isAllPiecesPlaced()) {

                int[] bestMove = TicTacToeAI.getBestMove(board);

                if (bestMove[0] != -1 &&
                        bestMove[1] != -1) {

                    placeTileAI(
                            -1,
                            -1,
                            bestMove[0],
                            bestMove[1]
                    );
                }

                return;
            }

            // FAS 2 - flytta pjäser
            for (int fromRow = 0; fromRow < 3; fromRow++) {
                for (int fromCol = 0; fromCol < 3; fromCol++) {

                    if ("R".equals(board[fromRow][fromCol])) {

                        for (int toRow = 0; toRow < 3; toRow++) {
                            for (int toCol = 0; toCol < 3; toCol++) {

                                if (board[toRow][toCol] == null &&
                                        isAdjacent(fromRow, fromCol, toRow, toCol)) {

                                    placeTileAI(
                                            fromRow,
                                            fromCol,
                                            toRow,
                                            toCol
                                    );

                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }).start();
    }
}