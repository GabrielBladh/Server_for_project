package chess;
import Game.Game;

public class Chess implements Game
{
    private Player currentPlayer = Player.WHITE;
    private static final Piece emptySpace = new Piece(Player.NONE, PieceType.NONE);
    private Piece[][] board = new Piece[8][8];
    private Player player;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean[][] validMove = new boolean[8][8];
    int[][] hästMoves = {
            { 2, 1}, { 2, -1},
            {-2, 1}, {-2, -1},
            { 1, 2}, { 1, -2},
            {-1, 2}, {-1, -2}
    };
    int[][] tornMoves = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };
    int[][] löpareMoves = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };
    int[][] drottningMoves = {
            { 1, 0}, {-1, 0},
            { 0, 1}, { 0,-1},
            { 1, 1}, { 1,-1},
            {-1, 1}, {-1,-1}
    };
    int[][] kungMoves = {
            {1, 0}, {1, 1}, {1, -1},
            {0, 1},         {0, -1},
            {-1, 0}, {-1, -1}, {-1, 1}
    };

    public Chess()
    {
        StartGame();
    }

    public void StartGame()
    {
        board[0][0] = new Piece(Player.WHITE, PieceType.TORN);
        board[0][1] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][2] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][3] = new Piece(Player.WHITE, PieceType.KUNG);
        board[0][4] = new Piece(Player.WHITE, PieceType.DROTTNING);
        board[0][5] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][6] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][7] = new Piece(Player.WHITE, PieceType.TORN);
        for (int col = 0; col < 8; col++)
        {
            board[1][col] = new Piece(Player.WHITE, PieceType.BONDE);
        }

        board[7][0] = new Piece (Player.BLACK, PieceType.TORN);
        board[7][1] = new Piece(Player.BLACK, PieceType.HÄST);
        board[7][2] = new Piece(Player.BLACK, PieceType.LÖPARE);
        board[7][3] = new Piece(Player.BLACK, PieceType.KUNG);
        board[7][4] = new Piece(Player.BLACK, PieceType.DROTTNING);
        board[7][5] = new Piece(Player.BLACK, PieceType.LÖPARE);
        board[7][6] = new Piece(Player.BLACK, PieceType.HÄST);
        board[7][7] = new Piece(Player.BLACK, PieceType.TORN);
        for (int col = 0; col < 8; col++)
        {
            board[6][col] = new Piece(Player.BLACK, PieceType.BONDE);
        }

        for (int row = 2; row < 6; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                board[row][col] = emptySpace;
            }
        }
    }

    public String getGameStatus()
    {
        String moves = ValidMovesString();
        return moves.replace('1', 'G').replace('0', 'N');
    }

    public String getBoardStatus()
    {
        String boardStatus = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isEmpty(row, col)) {
                    boardStatus += "N";
                }
                else if (board[row][col].getPiece() == PieceType.BONDE && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "B";
                }
                else if (board[row][col].getPiece() == PieceType.BONDE && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "b";
                }
                else if  (board[row][col].getPiece() == PieceType.HÄST && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "h";
                }
                else if (board[row][col].getPiece() == PieceType.HÄST && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "H";
                }
                else if (board[row][col].getPiece() == PieceType.KUNG && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "k";
                }
                else if (board[row][col].getPiece() == PieceType.KUNG && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "K";
                }
                else if  (board[row][col].getPiece() == PieceType.DROTTNING && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "d";
                }
                else if (board[row][col].getPiece() == PieceType.DROTTNING && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "D";
                }
                else if (board[row][col].getPiece() == PieceType.TORN && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "t";
                }
                else if (board[row][col].getPiece() == PieceType.TORN && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "T";
                }
                else if  (board[row][col].getPiece() == PieceType.LÖPARE && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "l";
                }
                else if (board[row][col].getPiece() == PieceType.LÖPARE && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "L";
                }
            }
        }
        return boardStatus;
    }

    public String ValidMovesString()
    {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                sb.append(validMove[row][col] ? '1' : '0');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean placeTile(int row, int col)
    {
        if (!isEmpty(row, col) && board[row][col].getOwner().equals(currentPlayer))
        {
            clearValidMoves();
            selectedRow = row;
            selectedCol = col;
            checkMoves(row, col);
            return true;
        }
        if (selectedRow != -1 && selectedCol != -1 && validMove[row][col])
        {
            Piece movingPiece = board[selectedRow][selectedCol];
            board[row][col] = movingPiece;
            board[selectedRow][selectedCol] = emptySpace;

            clearValidMoves();

            selectedRow = -1;
            selectedCol = -1;

            endTurn();
            return true;
        }
        return false;
    }

    public void checkMoves(int row, int col)
    {
        //Hur bonde kan röra sig
        if (board[row][col].getPiece() == PieceType.BONDE)
        {
            if (Player.WHITE.equals(board[row][col].getOwner()))
            {
                if (row + 1 < 8 && col + 1 < 8 && Player.BLACK.equals(board[row + 1][col + 1].getOwner()))
                {
                    markIfValid(row + 1, col + 1);
                }
                if (row + 1 < 8 && col - 1 >= 0 && Player.BLACK.equals(board[row + 1][col - 1].getOwner()))
                {
                    markIfValid(row + 1, col - 1);
                }
                if (row + 1 < 8 && isEmpty(row + 1, col))
                {
                    markIfValid(row + 1, col);

                    if (!board[row][col].getMoved() && row + 2 < 8 && isEmpty(row + 2, col)) {
                        markIfValid(row + 2, col);
                        board[row][col].setMoved();
                    }
                }
            }
            if (board[row][col].getOwner().equals(Player.BLACK))
            {
                if (row - 1 < 8 && col - 1 < 8 && Player.WHITE.equals(board[row - 1][col - 1].getOwner()))
                {
                    markIfValid(row - 1, col - 1);
                }
                if (row - 1 < 8 && col + 1 >= 0 && Player.WHITE.equals(board[row - 1][col + 1].getOwner()))
                {
                    markIfValid(row - 1, col + 1);
                }
                if (row - 1 < 8 && isEmpty(row - 1, col))
                {
                    markIfValid(row - 1, col);
                    if (!board[row][col].getMoved() && row - 2 >= 0 && isEmpty(row - 2, col)) {
                        markIfValid(row - 2, col);
                        board[row][col].setMoved();
                    }
                }
            }
        }

        //Hur häst kan röra sig
        else if (board[row][col].getPiece() == PieceType.HÄST)
        {
            for (int i = 0; i < hästMoves.length; i++)
            {
                int newRow = row + hästMoves[i][0];
                int newCol = col + hästMoves[i][1];

                markIfValid(newRow, newCol);
            }
        }

        //Hur torn kan röra sig
        else if (board[row][col].getPiece() == PieceType.TORN)
        {
            for (int i = 0; i < tornMoves.length; i++)
            {
                int dRow = tornMoves[i][0];
                int dCol = tornMoves[i][1];
                for (int step = 1; step < 8; step++)
                {
                    int newRow = row + dRow * step;
                    int newCol = col + dCol * step;
                    if (!markIfValidSliding(newRow, newCol))
                    {
                        break;
                    }
                }
            }
        }

        //Hur löpare kan röra sig
        else if (board[row][col].getPiece() == PieceType.LÖPARE)
        {
            for (int i = 0; i < löpareMoves.length; i++)
            {
                int dRow = löpareMoves[i][0];
                int dCol = löpareMoves[i][1];

                for (int step = 1; step < 8; step++)
                {
                    int newRow = row + dRow * step;
                    int newCol = col + dCol * step;

                    if (!markIfValidSliding(newRow, newCol))
                    {
                        break;
                    }
                }
            }
        }

        //Hur drottning kan röra sig
        else if (board[row][col].getPiece() == PieceType.DROTTNING)
        {
            for (int i = 0; i < drottningMoves.length; i++)
            {
                int dRow = drottningMoves[i][0];
                int dCol = drottningMoves[i][1];

                for (int step = 1; step < 8; step++)
                {
                    int newRow = row + dRow * step;
                    int newCol = col + dCol * step;

                    if (!markIfValidSliding(newRow, newCol))
                    {
                        break;
                    }
                }
            }
        }

        //Hur kung kan röra sig
        else if (board[row][col].getPiece() == PieceType.KUNG)
        {
            for (int i = 0; i < kungMoves.length; i++)
            {
                int newRow = row + kungMoves[i][0];
                int newCol = col + kungMoves[i][1];

                markIfValid(newRow, newCol);
            }
        }
    }

    public void markIfValid(int row, int col)
    {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
        {
            if (isEmpty(row, col) || !board[row][col].getOwner().equals(currentPlayer))
            {
                validMove[row][col] = true;
            }
        }
    }

    public boolean markIfValidSliding(int row, int col)
    {
        if (row < 0 || row >= 8 || col < 0 || col >= 8)
        {
            return false;
        }

        if (isEmpty(row, col))
        {
            validMove[row][col] = true;
            return true; // can continue sliding
        }

        if (!board[row][col].getOwner().equals(currentPlayer))
        {
            validMove[row][col] = true;
        }
        return false;
    }

    private void clearValidMoves(){
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++ )
            {
                if (validMove[row][col])
                {
                    validMove[row][col] = false;
                }
            }
        }
    }

    public void endTurn()
    {
        if (currentPlayer.equals(Player.WHITE))
        {
            currentPlayer = Player.BLACK;
        }
        else {
            currentPlayer = Player.WHITE;
        }
    }

    @Override
    public String getTurn()
    {
        return "";
    }

    @Override
    public String getGameEnd()
    {
        return "";
    }

    @Override
    public boolean isGameEnded()
    {
        return false;
    }

    private boolean isEmpty(int row, int col)
        {
        return board[row][col].getPiece() == PieceType.NONE;
    }
}
