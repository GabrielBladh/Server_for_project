public class TicTacToe implements Game{

    String[][] board = new String[3][3];
    String currentPlayer = "B";

    @Override
    public String getGameStatus() {
        String boardStatus = "";
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){
                if (board[row][col] == null) {
                    boardStatus += "N";
                }
                else if (board[row][col].equals("B")) {
                    boardStatus +="B";
                }
                else if (board[row][col].equals("R")) {
                    boardStatus += "R";
                }
            }
        }
        return boardStatus;
    }

    public String getTurn(){
        return currentPlayer;
    }

    @Override
    public boolean placeTile(int row, int col) {
        board[row][col] = currentPlayer;
        endTurn();
        return true;
    }

    public void endTurn(){
        if (currentPlayer.equals("B")){
            currentPlayer = "R";
        }
        else {
            currentPlayer = "B";
        }
    }
}
