public class TicTacToe implements Game{

    String[][] board = new String[3][3];
    String[][] boardBlink = new String[3][3];
    String currentPlayer = "B";
    Boolean isGameEnded = false;

    public TicTacToe()
    {
        boardBlink = new String[][]{{"0", "0","0"},{"0", "0", "0"},{"0", "0", "0"}};
    }

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

    public String getTurn()
    {
        return currentPlayer;
    }

    @Override
    public boolean placeTile(int row, int col) {
        if (isGameEnded == false)
        {
            if (board[row][col] == null)
            {
                board[row][col] = currentPlayer;
                endTurn();
                return true;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public void endTurn(){
        checkEndGame();
        if (currentPlayer.equals("B")){
            currentPlayer = "R";
        }
        else {
            currentPlayer = "B";
        }
    }

    @Override
    public String getGameEnd()
    {
        String boardStatusEnd = "";
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){
                if (boardBlink[row][col].equals("1"))
                {
                    boardStatusEnd += "1";
                }
                else
                {
                    boardStatusEnd += "0";
                }
            }
        }
        return boardStatusEnd;
    }

    @Override
    public boolean isGameEnded()
    {
        return isGameEnded;
    }

    public String checkEndGame()
    {
        for (int row = 0; row < board.length; row++)
        {
            if (board[row][0] != null &&
                    board[row][0].equals(board[row][1]) &&
                    board[row][1].equals(board[row][2]))
            {
                isGameEnded = true;
                for(int col = 0; col < board.length; col++)
                {
                    boardBlink[row][col] = "1";
                }
            }
        }
        for (int col = 0; col < board.length; col++)
        {
            if (board[0][col] != null &&
            board[0][col].equals(board[1][col]) &&
            board[1][col].equals(board[2][col]))
            {
                isGameEnded = true;
                for(int row = 0; row < board.length; row++)
                {
                    boardBlink[row][col] = "1";
                }
            }
        }

        // Check diagonals
        if (board[0][0] != null &&
        board[0][0].equals(board[1][1]) &&
        board[1][1].equals(board[2][2]))
        {
            isGameEnded = true;
            boardBlink[0][0] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][2] = "1";
        }

        if (board[0][2] != null &&
        board[0][2].equals(board[1][1]) &&
        board[1][1].equals(board[2][0]))
        {
            isGameEnded = true;
            boardBlink[0][2] = "1";
            boardBlink[1][1] = "1";
            boardBlink[2][0] = "1";
        }
        return null;
    }
}
