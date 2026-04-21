public class Checkers implements Game {

    String[][] board = new String[8][8];
    String currentPlayer = "B";

    int selectedRow = -1;
    int selectedCol = -1;
    String selectedPiece = "";

    int blueCounter = 12;
    int redCounter = 12;
    boolean isGameEnded = false;

    public Checkers() {
        setupGame();
    }

    public void endTurn(){
        if (currentPlayer.equals("B")) {
            currentPlayer = "R";
        }
        else {
            currentPlayer = "B";
        }
    }

    public String getTurn(){
        return currentPlayer;
    }

    @Override
    public String getGameEnd() {
        StringBuilder blinkStatus = new StringBuilder();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null) {
                    blinkStatus.append("1");
                } else {
                    blinkStatus.append("0");
                }
            }
        }
        return blinkStatus.toString();
    }

    @Override
    public boolean isGameEnded() {
        return isGameEnded;
    }

    public boolean placeTile(int row, int col) {
        // Spärr: Om spelet är slut, ignorera klick
        if (isGameEnded) {
            return false;
        }

        boolean isOwnPiece = false;
        if (board[row][col] != null) {
            if (currentPlayer.equals("B") && (board[row][col].equals("B") || board[row][col].equals("M"))) {
                isOwnPiece = true;
            }
            else if (currentPlayer.equals("R") && (board[row][col].equals("R") || board[row][col].equals("D"))) {
                isOwnPiece = true;
            }
        }

        if (isOwnPiece) {
            clearValidMoves();
            selectedRow = row;
            selectedCol = col;
            selectedPiece = board[row][col];
            checkMoves(row, col, selectedPiece);
            return true;
        }

        // Flytta vald pjäs
        if (selectedRow != -1 && selectedCol != -1 && "G".equals(board[row][col])){

            board[row][col] = selectedPiece;
            board[selectedRow][selectedCol] = null;

            // Om det var ett hopp
            if (Math.abs(row - selectedRow) == 2) {
                int capturedRow = (row + selectedRow) / 2;
                int capturedCol = (col + selectedCol) / 2;

                board[capturedRow][capturedCol] = null;
                scoreTracker(currentPlayer);
                System.out.println("Röda kvar: " + redCounter);
                System.out.println("Blå kvar: " + blueCounter);

                endGame();
            }

            // Spärr 2: Om draget ovanför avslutade spelet, måste vi avbryta direkt!
            if (isGameEnded) {
                return true;
            }

            checkPromotion(row, col);

            clearValidMoves();
            selectedRow = -1;
            selectedCol = -1;
            selectedPiece = "";
            endTurn();
            return true;
        }

        // Klick på tom ruta
        if (board[row][col] == null || board[row][col].equals("N")) {
            clearValidMoves();
            selectedRow = -1;
            selectedCol = -1;
            selectedPiece = "";
        }
        return false;
    }

    private void checkPromotion(int row, int col) {
        if (board[row][col] == null) return; // Säkerhetsspärr

        if (board[row][col].equals("B") && row == 7) {
            board[row][col] = "M";
        } else if (board[row][col].equals("R") && row == 0) {
            board[row][col] = "D";
        }
    }

    public void checkMoves(int row, int col, String piece) {
        boolean canMoveDown = piece.equals("B") || piece.equals("M") || piece.equals("D");
        boolean canMoveUp = piece.equals("R") || piece.equals("M") || piece.equals("D");

        String oppNormal;
        String oppKing;

        if (piece.equals("B") || piece.equals("M")) {
            oppNormal = "R";
            oppKing = "D";
        } else {
            oppNormal = "B";
            oppKing = "M";
        }

        if (canMoveDown) {
            markIfValid(row + 1, col - 1);
            markIfValid(row + 1, col + 1);
            markCaptureIfValid(row + 1, col - 1, row + 2, col - 2, oppNormal, oppKing);
            markCaptureIfValid(row + 1, col + 1, row + 2, col + 2, oppNormal, oppKing);
        }

        if (canMoveUp) {
            markIfValid(row - 1, col - 1);
            markIfValid(row - 1, col + 1);
            markCaptureIfValid(row - 1, col - 1, row - 2, col - 2, oppNormal, oppKing);
            markCaptureIfValid(row - 1, col + 1, row - 2, col + 2, oppNormal, oppKing);
        }
    }

    private void clearValidMoves(){
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++ ){
                if ("G".equals(board[r][c])){
                    board[r][c] = null;
                }
            }
        }
    }

    private void markIfValid(int r, int c) {
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (board[r][c] == null) {
                board[r][c] = "G";
            }
        }
    }

    private void scoreTracker(String currentPlayer){
        if (currentPlayer.equals("B")){
            redCounter--;
        }
        else {
            blueCounter--;
        }
    }

    private void markCaptureIfValid(int midRow, int midCol, int endRow, int endCol, String oppNormal, String oppKing){
        if (endRow >= 0 && endRow < 8 && endCol >= 0 && endCol < 8){
            String middleSquare = board[midRow][midCol];

            if(middleSquare != null && (middleSquare.equals(oppNormal) || middleSquare.equals(oppKing)) && board[endRow][endCol] == null){
                board[endRow][endCol] = "G";
            }
        }
    }

    private void endGame(){
        if(redCounter == 0){
            setWinner("B");
        }
        else if(blueCounter == 0) {
            setWinner("R");
        }
    }

    private void setWinner(String winner) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = null;
            }
        }
        // Rita W
        for (int r = 1; r <= 5; r++) {
            board[r][1] = winner;
            board[r][6] = winner;
        }
        board[6][2] = winner;
        board[5][3] = winner;
        board[5][4] = winner;
        board[6][5] = winner;

        // Din hårdvaru-signal för att spelet är slut på index 0,0
        board[0][0] = "W";

        isGameEnded = true;
    }

    public String getGameStatus() {
        StringBuilder boardStatus = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == null) {
                    boardStatus.append("N");
                } else {
                    // Denna rad löser kraschen! Den tvingar Java att alltid lägga till
                    // bokstaven, även om det är den speciella "W"-bokstaven.
                    // Hårdvaran får nu alltid exakt 64 tecken.
                    boardStatus.append(board[row][col]);
                }
            }
        }
        return boardStatus.toString();
    }

    public void setupGame() {
        board[0][1] = "B";
        board[0][3] = "B";
        board[0][5] = "B";
        board[0][7] = "B";
        board[1][0] = "B";
        board[1][2] = "B";
        board[1][4] = "B";
        board[1][6] = "B";
        board[2][1] = "B";
        board[2][3] = "B";
        board[2][5] = "B";
        board[2][7] = "B";
        board[7][0] = "R";
        board[7][2] = "R";
        board[7][4] = "R";
        board[7][6] = "R";
        board[6][1] = "R";
        board[6][3] = "R";
        board[6][5] = "R";
        board[6][7] = "R";
        board[5][0] = "R";
        board[5][2] = "R";
        board[5][4] = "R";
        board[5][6] = "R";
    }
}