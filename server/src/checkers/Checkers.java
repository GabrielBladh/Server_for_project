package checkers;
import Game.Game;


public class Checkers implements Game {

    String[][] board = new String[8][8];
    String[][] blinkBoard = new String[8][8];
    String currentPlayer = "B";

    int selectedRow = -1;
    int selectedCol = -1;
    String selectedPiece = "";
    public boolean isPlayingAgainstAI = false;

    int blueCounter = 12;
    int redCounter = 12;
    boolean isGameEnded = false;
    boolean multiJumpActive = false;
    boolean AIgame = false;

    public Checkers() {
        setupGame();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                blinkBoard[r][c] = "0";
            }
        }
    }

    public void setAI(boolean AIgame) {
        this.AIgame = AIgame;
    }

    public String getBoardStatus(){
        return "";
    }

    public void endTurn(){
        if (currentPlayer.equals("B")) {
            currentPlayer = "R";
        }
        else {
            currentPlayer = "B";
        }
        if (!hasValidMoves(currentPlayer)){
            if (currentPlayer.equals("B")){
                setWinner("R");
            } else {
                setWinner("B");
            }
        }

        if (AIgame && currentPlayer.equals("R") && !isGameEnded) {
            CheckersAI.doComputerMove(this);
        }
    }
    public String getTurn(){
        return currentPlayer;
    }

    @Override
    public String getGameEnd() {
        StringBuilder statusEnd = new StringBuilder();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                statusEnd.append(blinkBoard[r][c]);
            }
        }
        return statusEnd.toString();
    }

    @Override
    public boolean isGameEnded() {
        return isGameEnded;
    }

    public boolean placeTile(int row, int col) {
        if (isGameEnded) {
            return false;
        }

        boolean isOwnPiece = false;
        if (board[row][col] != null && !multiJumpActive) {
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

            // Kollar om spelaren är tvingad att hoppa
            boolean mustJump = doesPlayerHaveAnyJump(currentPlayer);
            checkMoves(row, col, selectedPiece, mustJump);
            return true;
        }

        if (selectedRow != -1 && selectedCol != -1 && "G".equals(board[row][col])){

            board[row][col] = selectedPiece;
            board[selectedRow][selectedCol] = null;
            boolean justJumped = false;

            if (Math.abs(row - selectedRow) == 2) {
                int capturedRow = (row + selectedRow) / 2;
                int capturedCol = (col + selectedCol) / 2;

                board[capturedRow][capturedCol] = null;
                scoreTracker(currentPlayer);
                System.out.println("Röda kvar: " + redCounter);
                System.out.println("Blå kvar: " + blueCounter);
                justJumped = true;
                checkWinByCounters();
            }

            if (isGameEnded) {
                return true;
            }

            boolean promotedToKing = checkPromotion(row, col);

            if (justJumped && !promotedToKing){
                clearValidMoves();
                checkMoves(row, col, selectedPiece, true);
                boolean canJumpAgain = false;
                for (int r = 0; r < 8; r++){
                    for (int c = 0; c < 8; c++){
                        if ("G".equals( board[r][c])) canJumpAgain = true;
                    }
                }
                if (canJumpAgain){
                    multiJumpActive = true;
                    selectedRow = row;
                    selectedCol = col;
                    if (AIgame && currentPlayer.equals("R")) {
                        CheckersAI.doComputerMove(this);
                    }
                    return true;
                }
            }

            multiJumpActive = false;
            clearValidMoves();
            selectedRow = -1;
            selectedCol = -1;
            selectedPiece = "";
            endTurn();
            return true;
        }

        if (board[row][col] == null || board[row][col].equals("N")) {
            if(!multiJumpActive){
                clearValidMoves();
                selectedRow = -1;
                selectedCol = -1;
                selectedPiece = "";
            }
        }
        return false;
    }

    private boolean checkPromotion(int row, int col) {
        if (board[row][col] == null) return false;

        if (board[row][col].equals("B") && row == 7) {
            board[row][col] = "M";
            return true;
        } else if (board[row][col].equals("R") && row == 0) {
            board[row][col] = "D";
            return true;
        }
        return false;
    }

    public void checkMoves(int row, int col, String piece, boolean onlyJumps) {
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
            if (!onlyJumps){
                markIfValid(row + 1, col - 1);
                markIfValid(row + 1, col + 1);
            }
            markCaptureIfValid(row + 1, col - 1, row + 2, col - 2, oppNormal, oppKing);
            markCaptureIfValid(row + 1, col + 1, row + 2, col + 2, oppNormal, oppKing);
        }

        if (canMoveUp) {
            if (!onlyJumps){
                markIfValid(row - 1, col - 1);
                markIfValid(row - 1, col + 1);
            }
            markCaptureIfValid(row - 1, col - 1, row - 2, col - 2, oppNormal, oppKing);
            markCaptureIfValid(row - 1, col + 1, row - 2, col + 2, oppNormal, oppKing);
        }
    }

    private boolean doesPlayerHaveAnyJump(String player){
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                String piece = board[r][c];
                if(piece != null){
                    boolean isOwnPiece = false;
                    if (player.equals("B") && (piece.equals("B") || piece.equals("M"))) isOwnPiece = true;
                    if (player.equals("R") && (piece.equals("R") || piece.equals("D"))) isOwnPiece = true;

                    if (isOwnPiece && canJump(r, c, piece)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canJump(int row, int col, String piece){
        boolean canMoveDown = piece.equals("B") || piece.equals("M") || piece.equals("D");
        boolean canMoveUp = piece.equals("R") || piece.equals("M") || piece.equals("D");

        String oppNormal = (piece.equals("B") || piece.equals("M")) ? "R" : "B";
        String oppKing = (piece.equals("B") || piece.equals("M")) ? "D" : "M";

        if (canMoveDown) {
            if (isValidCapture(row + 1, col - 1, row + 2, col - 2, oppNormal, oppKing)) return true;
            if (isValidCapture(row + 1, col + 1, row + 2, col + 2, oppNormal, oppKing)) return true;
        }

        if (canMoveUp) {
            if (isValidCapture(row - 1, col - 1, row - 2, col - 2, oppNormal, oppKing)) return true;
            if (isValidCapture(row - 1, col + 1, row - 2, col + 2, oppNormal, oppKing)) return true;
        }
        return false;
    }

    private boolean isValidCapture(int midRow, int midCol, int endRow, int endCol, String oppNormal, String oppKing){
        if (endRow >= 0 && endRow < 8 && endCol >= 0 && endCol < 8){
            String middleSquare = board[midRow][midCol];
            if (middleSquare != null && (middleSquare.equals(oppNormal) ||
                    middleSquare.equals(oppKing)) && board[endRow][endCol] == null){
                return true;
            }
        }
        return false;
    }

    private boolean hasValidMoves(String player){
        boolean hasMove = false;
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                String piece = board[r][c];
                if (piece != null){
                    boolean isOwnPiece = false;
                    if (player.equals("B") && (piece.equals("B") || piece.equals("M"))) isOwnPiece = true;
                    if (player.equals("R") && (piece.equals("R") || piece.equals("D") )) isOwnPiece = true;
                    if (isOwnPiece){
                        checkMoves(r, c, piece, false);
                    }
                }
            }
        }
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if ("G".equals(board[r][c])){
                    hasMove = true;
                    break;
                }
            }
        }
        clearValidMoves();
        return hasMove;
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

    private void checkWinByCounters(){
        if(redCounter == 0){
            setWinner("B");
        }
        else if(blueCounter == 0) {
            setWinner("R");
        }
    }

    private void setWinner(String winner) {
        for (int r = 1; r <= 5; r++) {
            blinkBoard[r][1] = "1";
            blinkBoard[r][6] = "1";
        }
        blinkBoard[6][2] = "1";
        blinkBoard[5][3] = "1";
        blinkBoard[5][4] = "1";
        blinkBoard[6][5] = "1";
        isGameEnded = true;
    }

    public String getGameStatus() {
        StringBuilder boardStatus = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == null) {
                    boardStatus.append("N");
                } else {
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