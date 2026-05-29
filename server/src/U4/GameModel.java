package U4;

import Game.Game;

import java.io.IOException;
import java.util.Random;

/**
 * Huvudklassen för spelets logik (Model).
 * Hanterar spelplanen, turer, regler, poängberäkning och mysterier.
 */
public class GameModel implements Game {
    private String lastMystery = ""; // Fixat null-felet!
    private GamePiece[][] board;
    private Player currentPlayer;
    private int rows = 8;
    private int cols = 8;
    private int player1Score;
    private int player2Score;
    private boolean extraTurn = false;

    public GameModel() {
        startNewGame();
    }

    public void startNewGame() {
        board = new GamePiece[rows][cols];
        currentPlayer = Player.PLAYER1;
        player1Score = 0;
        player2Score = 0;
        placeMysteries();
    }

    private void placeMysteries() {
        Random rand = new Random();
        int mysteriesPlaced = 0;
        String[] mysteryTypes = {"Tidshopp", "Avgrundsvrål", "Additiva"};
        int attempts = 0;

        while (mysteriesPlaced < 5 && attempts < 1000) {
            attempts++;
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);

            boolean isCorner = (r==0 && c==0) || (r==0 && c==cols-1) ||
                    (r==rows-1 && c==0) || (r==rows-1 && c==cols-1);

            if (board[r][c] == null && !isCorner && !hasMysteryNeighbor(r, c)) {
                String type = mysteryTypes[rand.nextInt(mysteryTypes.length)];
                board[r][c] = new MysteryPiece(Player.NONE, type);
                mysteriesPlaced++;
            }
        }
    }

    private boolean hasMysteryNeighbor(int r, int c) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int nr = r + i;
                int nc = c + j;
                if (isValidPosition(nr, nc)) {
                    GamePiece p = board[nr][nc];
                    if (p instanceof MysteryPiece) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean placeTile(int row, int col){
        if (isGameEnded()) return false;

        if (!isValidPosition(row, col) || board[row][col] != null) {
            return false;
        }

        if (!isFirstMoveOverall() && !hasNeighbor(row, col)) {
            return false;
        }

        board[row][col] = new NormalPiece(currentPlayer);
        extraTurn = false;

        handleSurprise(row, col);
        calculateScore();

        if (!extraTurn) {
            currentPlayer = currentPlayer.getOpponent();
        }

        return true;
    }

    private void handleSurprise(int r, int c) {
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                checkDirection(r, c, dRow, dCol);
            }
        }
    }

    private void checkDirection(int r, int c, int dRow, int dCol) {
        int currentRow = r + dRow;
        int currentCol = c + dCol;
        boolean foundOpponent = false;

        while (isValidPosition(currentRow, currentCol)) {
            GamePiece piece = board[currentRow][currentCol];

            if (piece == null) {
                return;
            } else if (piece.getOwner() == currentPlayer.getOpponent()) {
                foundOpponent = true;
            } else if (piece.getOwner() == currentPlayer) {
                if (foundOpponent) {
                    flipPieces(r, c, dRow, dCol, currentRow, currentCol);
                }
                return;
            } else if (piece.getOwner() == Player.NONE) {
                foundOpponent = true;
            }
            currentRow += dRow;
            currentCol += dCol;
        }
    }

    private void flipPieces(int startR, int startC, int dRow, int dCol, int endR, int endC) {
        int r = startR + dRow;
        int c = startC + dCol;

        while (r != endR || c != endC) {
            GamePiece piece = board[r][c];
            if (piece != null) {
                piece.setOwner(currentPlayer);

                if (piece instanceof MysteryPiece) {
                    MysteryPiece mp = (MysteryPiece) piece;
                    if (!mp.isActivated()) {
                        activateMystery(mp, r, c);
                    }
                }
            }
            r += dRow;
            c += dCol;
        }
    }

    private void activateMystery(MysteryPiece m, int r, int c) {
        m.setActivated(true);
        String type = m.getMysteryType();

        switch (type) {
            case "Avgrundsvrål":
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        int nr = r + i;
                        int nc = c + j;
                        if (isValidPosition(nr, nc)) {
                            board[nr][nc] = null;
                        }
                    }
                }
                break;
            case "Additiva":
                int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                for (int[] offset : offsets) {
                    int nr = r + offset[0];
                    int nc = c + offset[1];
                    if (isValidPosition(nr, nc) && board[nr][nc] == null) {
                        board[nr][nc] = new NormalPiece(currentPlayer);
                    } else if (isValidPosition(nr, nc) && board[nr][nc] != null) {
                        board[nr][nc].setOwner(currentPlayer);
                    }
                }
                break;
            case "Tidshopp":
                this.extraTurn = true;
                break;
        }
    }
@Override
    public boolean isGameEnded() {
        boolean boardIsFull = true;
        boolean allMysteriesActivated = true;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                GamePiece p = board[r][c];
                if (p == null) {
                    boardIsFull = false;
                }
                if (p instanceof MysteryPiece) {
                    if (!((MysteryPiece) p).isActivated()) {
                        allMysteriesActivated = false;
                    }
                }
            }
        }
        return boardIsFull || allMysteriesActivated;
    }

    private boolean isValidPosition(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private boolean hasNeighbor(int r, int c) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i==0 && j==0) continue;
                int nr = r + i;
                int nc = c + j;
                if (isValidPosition(nr, nc) && board[nr][nc] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFirstMoveOverall() {
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(board[i][j] != null && board[i][j].getOwner() != Player.NONE){
                    return false;
                }
            }
        }
        return true;
    }

    private void calculateScore() {
        player1Score = 0;
        player2Score = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getOwner() == Player.PLAYER1) player1Score++;
                    else if (board[i][j].getOwner() == Player.PLAYER2) player2Score++;
                }
            }
        }
    }

    @Override
    public String getTurn() {
        if (currentPlayer == Player.PLAYER1) {
            return "B";
        } else if (currentPlayer == Player.PLAYER2) {
            return "R";
        }
        return "N";
    }
    @Override
    public String getGameEnd() {
        if (!isGameEnded()) {
            return "0";
        }


        calculateScore();

        if (player1Score > player2Score) {
            return "1"; // Spelare 1 vann
        } else if (player2Score > player1Score) {
            return "2"; // Spelare 2 vann
        } else {
            return "T"; // Tie (Oavgjort)
        }
    }

    @Override
    public String getGameStatus() {
        // Skickar ENDAST brädet nu. Inga popups som kan krascha klienten.
        return getBoardStatus();
    }

    @Override
    public String getBoardStatus() {
        StringBuilder status = new StringBuilder();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                GamePiece p = board[r][c];

                if (p == null) {
                    status.append("N");
                }
                else if (p instanceof MysteryPiece) {
                    MysteryPiece mp = (MysteryPiece) p;
                    if (mp.isActivated()) {
                        status.append("A");
                    } else {
                        status.append("G"); // Visas GRÖN i klienten
                    }
                }
                else {
                    if (p.getOwner() == Player.PLAYER1) {
                        status.append("B"); // Visas BLÅ
                    } else if (p.getOwner() == Player.PLAYER2) {
                        status.append("R"); // Visas RÖD
                    } else {
                        status.append("N");
                    }
                }
            }
        }
        return status.toString();
    }

    @Override
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
}