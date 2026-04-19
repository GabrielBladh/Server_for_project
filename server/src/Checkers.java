public class Checkers implements Game {

    String[][] board = new String[8][8];
    String currentPlayer = "B";

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

    public boolean placeTile(int row, int col) {
        // först ska man kolla om rutan är inte tom för att förhindra NullPointerException
        if (board[row][col] !=null && board[row][col].equals("B")) {

            // < istället för >, då ska man kolla upp till inder 7 (8 platser på brädet)
            if (row + 1 < 8) {

                //samma sak här
                if (col + 1 < 8 && board[row+1][col-1] != null) {

                    board[row+1][col+1] = "G";
                }
                if (col - 1  >= 0 && board[row+1][col-1] != null) {
                    board[row + 1][col - 1] = "G";
                }
            }
            return true;
        }
        return false;
    }

    public void checkMoves(int row, int col, String player) {

    }


    public String getGameStatus() {
        String boardStatus = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == null) {
                    boardStatus += "N";
                }
                else if (board[row][col].equals("B")) {
                    boardStatus +="B";
                }
                else if  (board[row][col].equals("W")) {
                    boardStatus += "W";
                }
                else if (board[row][col].equals("R")) {
                    boardStatus += "R";
                }
            }
        }
        return boardStatus;
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
