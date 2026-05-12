package chess;
import Game.Game;

import java.util.Objects;
import java.util.Scanner;

public class Chess implements Game {
    private boolean AIgame = false; // Från dig
    private StockfishEngine engine; // Från dig
    private Player currentPlayer = Player.WHITE;
    private static final Piece emptySpace = new Piece(Player.NONE, PieceType.NONE);
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int enPassantRow = -1; // Från din kompis
    private int enPassantCol = -1; // Från din kompis
    private Player enPassantOwner = Player.NONE; // Från din kompis
    private String[][] validMove = new String[8][8];
    private boolean promotionMode = false;
    private int promotionRow = -1;
    private int promotionCol = -1;

    int[][] hästMoves = {
            {2, 1}, {2, -1},
            {-2, 1}, {-2, -1},
            {1, 2}, {1, -2},
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
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
    int[][] kungMoves = {
            {1, 0}, {1, 1}, {1, -1},
            {0, 1}, {0, -1},
            {-1, 0}, {-1, -1}, {-1, 1}
    };

    public Chess() {
        StartGame();
    }

    public void StartGame() {
        board[0][0] = new Piece(Player.WHITE, PieceType.TORN);
        board[0][1] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][2] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][3] = new Piece(Player.WHITE, PieceType.DROTTNING); // <-- SKA VARA INDEX 3 (d1)
        board[0][4] = new Piece(Player.WHITE, PieceType.KUNG);      // <-- SKA VARA INDEX 4 (e1)
        board[0][5] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][6] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][7] = new Piece(Player.WHITE, PieceType.TORN);
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Piece(Player.WHITE, PieceType.BONDE);
        }

        board[7][0] = new Piece(Player.BLACK, PieceType.TORN);
        board[7][1] = new Piece(Player.BLACK, PieceType.HÄST);
        board[7][2] = new Piece(Player.BLACK, PieceType.LÖPARE);
        board[7][3] = new Piece(Player.BLACK, PieceType.DROTTNING); // <-- SKA VARA INDEX 3 (d8)
        board[7][4] = new Piece(Player.BLACK, PieceType.KUNG);      // <-- SKA VARA INDEX 4 (e8)
        board[7][5] = new Piece(Player.BLACK, PieceType.LÖPARE);
        board[7][6] = new Piece(Player.BLACK, PieceType.HÄST);
        board[7][7] = new Piece(Player.BLACK, PieceType.TORN);
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Piece(Player.BLACK, PieceType.BONDE);
        }

        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = emptySpace;
            }
        }
    }

    public String getGameStatus()
    {
        return ValidMovesString();
    }


    public String getBoardStatus() {
        String boardStatus = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isEmpty(row, col)) {
                    boardStatus += "N";
                } else if (board[row][col].getPiece() == PieceType.BONDE && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "B";
                } else if (board[row][col].getPiece() == PieceType.BONDE && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "b";
                } else if (board[row][col].getPiece() == PieceType.HÄST && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "h";
                } else if (board[row][col].getPiece() == PieceType.HÄST && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "H";
                } else if (board[row][col].getPiece() == PieceType.KUNG && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "k";
                } else if (board[row][col].getPiece() == PieceType.KUNG && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "K";
                } else if (board[row][col].getPiece() == PieceType.DROTTNING && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "d";
                } else if (board[row][col].getPiece() == PieceType.DROTTNING && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "D";
                } else if (board[row][col].getPiece() == PieceType.TORN && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "t";
                } else if (board[row][col].getPiece() == PieceType.TORN && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "T";
                } else if (board[row][col].getPiece() == PieceType.LÖPARE && board[row][col].getOwner() == Player.BLACK) {
                    boardStatus += "l";
                } else if (board[row][col].getPiece() == PieceType.LÖPARE && board[row][col].getOwner() == Player.WHITE) {
                    boardStatus += "L";
                }
            }
        }
        return boardStatus;
    }

    public String ValidMovesString() {
        String validMovesStringBuilder = "";

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (validMove[row][col] == null) {
                    validMovesStringBuilder += "N";
                } else if (validMove[row][col].equals("B"))
                {
                    validMovesStringBuilder += "B";
                } else if (validMove[row][col].equals("R")) {
                    validMovesStringBuilder += "R";
                } else if (validMove[row][col].equals("G")) {
                    validMovesStringBuilder += "G";
                }
                else if (validMove[row][col].equals("L"))
                {
                    validMovesStringBuilder += "L";
                }
                else if (validMove[row][col].equals("Y"))
                {
                    validMovesStringBuilder += "Y";
                }
                else if (validMove[row][col].equals("O"))
                {
                    validMovesStringBuilder += "O";
                }
                else if (validMove[row][col].equals("P"))
                {
                    validMovesStringBuilder += "P";
                }
            }
        }
        return validMovesStringBuilder;
    }

    @Override
    public boolean placeTile(int row, int col)
    {
        if (promotionMode)
        {
            Piece newPiece = bondeChangesPiece(row, col);

            if (newPiece != null)
            {
                board[promotionRow][promotionCol] = newPiece;
                promotionMode = false;
                clearValidMoves();
                selectedRow = -1;
                selectedCol = -1;
                endTurn();
            }
            return true;
        }

        if (!isEmpty(row, col) && board[row][col].getOwner().equals(currentPlayer)) {
            checkIfKingChecked(currentPlayer);

            clearValidMoves();
            selectedRow = row;
            selectedCol = col;

            markBlue(selectedRow, selectedCol);
            checkMoves(row, col);
            return true;
        }
        if (validMove[row][col] == null) {
            return false;
        }

        if (selectedRow != -1 && selectedCol != -1 &&
                (validMove[row][col].equals("G") || validMove[row][col].equals("R"))) {

            board[selectedRow][selectedCol].setMoved();
            int fromRow = selectedRow;
            int fromCol = selectedCol;

            if (isEnPassant(fromRow, fromCol, row, col)) {
                executeEnPassant(row, col);
            }

            Piece movingPiece = board[fromRow][fromCol];
            if (movingPiece.getPiece() == PieceType.KUNG && Math.abs(col - selectedCol) == 2) {
                if (col > selectedCol) {
                    Piece rook = board[selectedRow][selectedCol + 3];
                    board[selectedRow][selectedCol + 1] = rook;
                    board[selectedRow][selectedCol + 3] = emptySpace;
                    rook.setMoved();
                } else {
                    Piece rook = board[selectedRow][selectedCol - 4];
                    board[selectedRow][selectedCol - 1] = rook;
                    board[selectedRow][selectedCol - 4] = emptySpace;
                    rook.setMoved();
                }
            }

            board[row][col] = movingPiece;
            board[fromRow][fromCol] = emptySpace;
            if (movingPiece.getPiece() == PieceType.BONDE &&
                    (row == 0 || row == 7))
            {
                promotionMode = true;

                promotionRow = row;
                promotionCol = col;

                clearValidMoves();

                markChangeBondeValid();

                return true;
            }

            clearValidMoves();
            clearEnPassant();
            registerEnPassant(fromRow, fromCol, row, col);



            selectedRow = -1;
            selectedCol = -1;

            endTurn();
            return true;
        }
        return false;
    }

    public void checkMoves(int row, int col) {
        //Hur bonde kan röra sig
        if (board[row][col].getPiece() == PieceType.BONDE) {
            if (Player.WHITE.equals(board[row][col].getOwner())) {
                if (row + 1 < 8 && col + 1 < 8 && Player.BLACK.equals(board[row + 1][col + 1].getOwner()))
                {
                    if (!wouldLeaveKingInCheck(row, col, row + 1, col + 1))
                    {
                        markIfValid(row + 1, col + 1);
                    }
                }
                if (row + 1 < 8 && col - 1 >= 0 && Player.BLACK.equals(board[row + 1][col - 1].getOwner()))
                {
                    if (!wouldLeaveKingInCheck(row, col, row + 1, col - 1))
                    {
                        markIfValid(row + 1, col - 1);
                    }
                }
                if (row + 1 < 8 && isEmpty(row + 1, col))
                {
                    if (!wouldLeaveKingInCheck(row, col, row + 1, col))
                    {
                        markIfValid(row + 1, col);
                    }
                    if (!board[row][col].getisMoved() && row + 2 < 8 && isEmpty(row + 2, col))
                    {
                        if (!wouldLeaveKingInCheck(row, col, row + 2, col))
                        {
                            markIfValid(row + 2, col);
                        }
                    }
                }
            }
            if (board[row][col].getOwner().equals(Player.BLACK)) {
                if (row - 1 < 8 && col - 1 >= 0 && Player.WHITE.equals(board[row - 1][col - 1].getOwner()))
                {
                    if (!wouldLeaveKingInCheck(row, col, row - 1, col - 1))
                    {
                        markIfValid(row - 1, col - 1);
                    }
                }
                if (row - 1 < 8 && col + 1 < 8 && Player.WHITE.equals(board[row - 1][col + 1].getOwner()))
                {
                    if (!wouldLeaveKingInCheck(row, col, row - 1, col + 1))
                    {
                        markIfValid(row - 1, col + 1);
                    }
                }
                if (row - 1 < 8 && isEmpty(row - 1, col))
                {
                    if (!wouldLeaveKingInCheck(row, col, row - 1, col))
                    {
                        markIfValid(row - 1, col);
                    }
                    if (!board[row][col].getisMoved() && row - 2 >= 0 && isEmpty(row - 2, col))
                    {
                        if (!wouldLeaveKingInCheck(row, col, row - 2, col))
                        {
                            markIfValid(row - 2, col);
                        }
                    }
                }
            }
            if (board[row][col].getOwner() == Player.WHITE && row == 4)
            {
                if (!wouldLeaveKingInCheck(row, col, row, col + 1))
                {
                    markEnPassantIfValid(row, col, +1);
                }
            }

            if (board[row][col].getOwner() == Player.BLACK && row == 3)
            {
                if (!wouldLeaveKingInCheck(row, col, row, col - 1))
                {
                    markEnPassantIfValid(row, col, -1);
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


                if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                {
                    markIfValid(newRow, newCol);
                }
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

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8)
                    {
                        break;
                    }

                    Piece target = board[newRow][newCol];

                    if (target != null && target.getOwner().equals(currentPlayer))
                    {
                        break;
                    }
                    if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                    {
                        if (!markIfValidSliding(newRow, newCol))
                        {
                            break;
                        }
                    }

                    if (target != null && target.getPiece() != PieceType.NONE)
                    {
                        break;
                    }
                }
            }
        }

        //Hur löpare kan röra sig
        else if (board[row][col].getPiece() == PieceType.LÖPARE) {
            for (int i = 0; i < löpareMoves.length; i++) {
                int dRow = löpareMoves[i][0];
                int dCol = löpareMoves[i][1];


                for (int step = 1; step < 8; step++)
                {
                    int newRow = row + dRow * step;
                    int newCol = col + dCol * step;

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8)
                    {
                        break;
                    }
                    Piece target = board[newRow][newCol];

                    if (target != null && target.getOwner().equals(currentPlayer))
                    {
                        break;
                    }
                    if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                    {
                        if (!markIfValidSliding(newRow, newCol))
                        {
                            break;
                        }
                    }

                    if (target != null && target.getPiece() != PieceType.NONE)
                    {
                        break;
                    }
                }
            }
        }

        //Hur drottning kan röra sig
        else if (board[row][col].getPiece() == PieceType.DROTTNING) {
            for (int i = 0; i < drottningMoves.length; i++) {
                int dRow = drottningMoves[i][0];
                int dCol = drottningMoves[i][1];

                for (int step = 1; step < 8; step++) {
                    int newRow = row + dRow * step;
                    int newCol = col + dCol * step;

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8)
                    {
                        break;
                    }

                    Piece target = board[newRow][newCol];

                    if (target != null && target.getOwner().equals(currentPlayer))
                    {
                        break;
                    }
                    if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                    {
                        if (!markIfValidSliding(newRow, newCol))
                        {
                            break;
                        }
                    }

                    if (target != null && target.getPiece() != PieceType.NONE)
                    {
                        break;
                    }
                }
            }
        }

        //Hur kung kan röra sig
        else if (board[row][col].getPiece() == PieceType.KUNG) {
            for (int i = 0; i < kungMoves.length; i++) {
                int newRow = row + kungMoves[i][0];
                int newCol = col + kungMoves[i][1];

                if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                {
                    markIfValid(newRow, newCol);
                }
                Player enemyPlayer = (currentPlayer == Player.WHITE)
                        ? Player.BLACK
                        : Player.WHITE;
                //Castling
                if (!board[row][col].getisMoved() &&
                        col - 4 >= 0 &&
                        !board[row][col - 4].getisMoved() &&
                        board[row][col - 4].getPiece().equals(PieceType.TORN) &&
                        isEmpty(row, col - 1) && // d-linjen tom
                        isEmpty(row, col - 2) && // c-linjen tom
                        isEmpty(row, col - 3) && // b-linjen tom
                        !checkIfKingChecked(currentPlayer) &&
                        !isSquareAttacked(row, col - 1, enemyPlayer) &&
                        !isSquareAttacked(row, col - 2, enemyPlayer))
                {
                    int newCol2 = col - 2;
                    if (!wouldLeaveKingInCheck(row, col, row, newCol2)) {
                        markIfValid(row, newCol2);
                    }
                }

                if (!board[row][col].getisMoved() &&
                        col + 3 < 8 &&
                        !board[row][col + 3].getisMoved() &&
                        board[row][col + 3].getPiece().equals(PieceType.TORN) &&
                        isEmpty(row, col + 1) && // f-linjen tom
                        isEmpty(row, col + 2) && // g-linjen tom
                        !checkIfKingChecked(currentPlayer) &&
                        !isSquareAttacked(row, col + 1, enemyPlayer) &&
                        !isSquareAttacked(row, col + 2, enemyPlayer))
                {
                    int newCol2 = col + 2;
                    if (!wouldLeaveKingInCheck(row, col, row, newCol2)) {
                        markIfValid(row, newCol2);
                    }
                }
            }
        }
    }

    public void markIfValid(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            if (isEmpty(row, col)) {
                validMove[row][col] = "G";
            } else if (!board[row][col].getOwner().equals(currentPlayer)) {
                validMove[row][col] = "R";
            }
        }
    }

    public boolean markIfValidSliding(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return false;
        }

        if (isEmpty(row, col)) {
            validMove[row][col] = "G";
            return true;
        }

        if (!board[row][col].getOwner().equals(currentPlayer)) {
            validMove[row][col] = "R";
        }
        return false;
    }

    private void markBlue(int row, int col) {
        validMove[row][col] = "B";
    }

    private void clearValidMoves() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (validMove[row][col] != null) {
                    if (validMove[row][col].equals("G") || validMove[row][col].equals("B") || validMove[row][col].equals("R") || validMove[row][col].equals("L") || validMove[row][col].equals("Y") || validMove[row][col].equals("O") || validMove[row][col].equals("P")) {
                        validMove[row][col] = null;
                    }
                }
            }
        }
    }

    public void endTurn() {
        if (currentPlayer.equals(Player.WHITE)) {
            currentPlayer = Player.BLACK;
        } else {
            currentPlayer = Player.WHITE;
        }

        if (isAITurn()) {
            doComputerMove();
        }
    }

    public boolean isAITurn() {
        return AIgame && currentPlayer == Player.BLACK;
    }

    public boolean checkIfKingChecked(Player currentPlayer) {
        int kingPositionRow = -1;
        int kingPositionCol = -1;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                if (!isEmpty(row, col)
                        && board[row][col].getPiece().equals(PieceType.KUNG)
                        && board[row][col].getOwner().equals(currentPlayer)) {
                    kingPositionRow = row;
                    kingPositionCol = col;
                }
            }
        }
        if (kingPositionCol == -1 || kingPositionRow == -1) {
            return false;
        } else {
            Player enemyPlayer = (currentPlayer == Player.WHITE)
                    ? Player.BLACK
                    : Player.WHITE;
            return isSquareAttacked(kingPositionRow, kingPositionCol, enemyPlayer);
        }
    }


    public boolean isSquareAttacked(int kingPositionRow, int kingPositionCol, Player enemyPlayer) {
        //Checka om bonde attackerar kungen
        int direction;
        if (enemyPlayer == Player.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }
        int pawnRow = kingPositionRow - direction;
        if (pawnRow >= 0 && pawnRow < board.length) {
            if (kingPositionCol - 1 >= 0) {
                Piece piece = board[pawnRow][kingPositionCol - 1];
                if (piece != null && piece.getOwner().equals(enemyPlayer)
                        && piece.getPiece().equals(PieceType.BONDE)) {
                    return true;
                }
            }


            if (kingPositionCol + 1 < board[0].length) {
                Piece piece = board[pawnRow][kingPositionCol + 1];
                if (piece != null && piece.getOwner().equals(enemyPlayer)
                        && piece.getPiece() == PieceType.BONDE) {
                    return true;
                }
            }
        }

        //Checka om häst attackerar kungen
        for (int[] moves : hästMoves) {
            int row = kingPositionRow + moves[0];
            int col = kingPositionCol + moves[1];

            if (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
                Piece piece = board[row][col];
                if (piece != null && piece.getOwner().equals(enemyPlayer) && piece.getPiece().equals(PieceType.HÄST)) {
                    return true;
                }
            }
        }

        for (int[] d : drottningMoves) {
            int row = kingPositionRow + d[0];
            int col = kingPositionCol + d[1];
            while (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPiece() != PieceType.NONE) {
                    if (piece.getOwner().equals(enemyPlayer)) {
                        boolean isStraight = d[0] == 0 || d[1] == 0;
                        boolean isDiagonal = Math.abs(d[0]) == Math.abs(d[1]) && d[0] != 0;
                        if (isStraight && (piece.getPiece().equals(PieceType.TORN) || piece.getPiece().equals(PieceType.DROTTNING))) {
                            return true;
                        }
                        if (isDiagonal && (piece.getPiece().equals(PieceType.LÖPARE) || piece.getPiece().equals(PieceType.DROTTNING))) {
                            return true;
                        }
                    }
                    break;
                }
                row += d[0];
                col += d[1];
            }
        }
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (row == 0 && col == 0) {
                    continue;
                }
                int nr = kingPositionRow + row;
                int nc = kingPositionCol + col;
                if (nr >= 0 && nr < board.length && nc >= 0 && nc < board.length) {
                    Piece piece = board[nr][nc];
                    if (piece != null && piece.getOwner().equals(enemyPlayer) && piece.getPiece().equals(PieceType.KUNG)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean wouldLeaveKingInCheck(int fromRow, int fromCol, int toRow, int toCol)
    {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8)
        {
            return true;
        }
        Piece movingPiece = board[fromRow][fromCol];
        Piece captured = board[toRow][toCol];

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = emptySpace;

        boolean inCheck = checkIfKingChecked(movingPiece.getOwner());

        board[fromRow][fromCol] = movingPiece;
        board[toRow][toCol] = captured;

        return inCheck;
    }

    public void stopAI() {
        if (AIgame && engine != null) {
            System.out.println("Stänger ner Stockfish...");
            engine.stopEngine();
        }
    }


    @Override
    public String getTurn() {
        return "";
    }

    @Override
    public String getGameEnd() {
        return "";
    }

    @Override
    public boolean isGameEnded() {
        return false;
    }

    private boolean isEmpty(int row, int col) {
        return board[row][col].getPiece() == PieceType.NONE;
    }

    public String getFEN() {
        StringBuilder fen = new StringBuilder();

        // 1. Läs av brädet (Från rad 7 ner till rad 0)
        // OBS: I traditionellt schack är rad 8 högst upp. Din kod har svart på rad 7.
        for (int row = 7; row >= 0; row--) {
            int emptySquares = 0;
            for (int col = 0; col < 8; col++) {
                Piece currentPiece = board[row][col];

                if (currentPiece.getPiece() == PieceType.NONE) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    fen.append(getFenCharacter(currentPiece));
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares);
            }
            if (row > 0) {
                fen.append("/");
            }
        }

        // 2. Lägg till vems tur det är
        fen.append(currentPlayer == Player.WHITE ? " w " : " b ");

        // 3. Lägg till rockad-möjligheter (Vi lägger in default "KQkq" för tillfället)
        // Om du inte har kodat rockad i din spelmotor än, låt denna vara "KQkq" eller "-"
        fen.append("KQkq ");

        // 4. En Passant (Vi ignorerar detta just nu och sätter "-")
        fen.append("- ");

        // 5. Halvdragsklocka och fullt dragnummer (Vi hårdkodar 0 1 för tillfället)
        fen.append("0 1");

        return fen.toString();
    }

    private char getFenCharacter(Piece piece) {
        boolean isWhite = piece.getOwner() == Player.WHITE;
        switch (piece.getPiece()) {
            case BONDE:
                return isWhite ? 'P' : 'p';
            case TORN:
                return isWhite ? 'R' : 'r';
            case HÄST:
                return isWhite ? 'N' : 'n'; // OBS! N för kNight i FEN
            case LÖPARE:
                return isWhite ? 'B' : 'b'; // OBS! B för Bishop i FEN
            case DROTTNING:
                return isWhite ? 'Q' : 'q';
            case KUNG:
                return isWhite ? 'K' : 'k';
            default:
                return '?';
        }
    }

    public void setAI(boolean isAI) {
        this.AIgame = isAI;
        if (isAI) {
            engine = new StockfishEngine();
            String macPath = "/Users/hooje/Documents/stockfish/stockfish-macos-m1-apple-silicon";
            engine.startEngine(macPath);

            engine.sendCommand("uci");
            // Aktivera begränsningen
            engine.sendCommand("setoption name UCI_LimitStrength value true");
            // Sätt Elo till lägsta möjliga (1320 är minimum i Stockfish)
            engine.sendCommand("setoption name UCI_Elo value 1320");
        }
    }
    private void doComputerMove() {
        new Thread(() -> {
            try {
                // Skapa FEN-strängen från det aktuella brädet
                String fen = getFEN();
                System.out.println("AI läser brädet som: " + fen);

                String bestMove = engine.getBestMove(fen, 50);
                System.out.println("Stockfish säger: " + bestMove);

                // bestMove ser ut så här: "e2e4" eller "g8f6"
                if (bestMove != null && bestMove.length() >= 4) {

                    // bokstäverna a-h är kolumnerna 0-7
                    int fromCol = bestMove.charAt(0) - 'a';
                    int toCol = bestMove.charAt(2) - 'a';

                    // siffrorna 1-8 är raderna (0-7, fast upp och ner i din logik)
                    // (Observera att detta kan behöva justeras beroende på om rad 0 är vit eller svart hos dig)
                    int fromRow = Character.getNumericValue(bestMove.charAt(1)) - 1;
                    int toRow = Character.getNumericValue(bestMove.charAt(3)) - 1;

                    // Genomför draget direkt på brädet!
                    System.out.println("AI flyttar från [" + fromRow + "," + fromCol + "] till [" + toRow + "," + toCol + "]");
                    placeTileAI(fromRow, fromCol, toRow, toCol);
                }

            } catch (Exception e) {
                System.out.println("Fel i AI-tråden: " + e.getMessage());
            }
        }).start();
    }

    public void placeTileAI(int fromRow, int fromCol, int toRow, int toCol) {
        if (isGameEnded()) return;

        clearValidMoves();

        validMove[fromRow][fromCol] = "B"; // Visar rutan pjäsen STÅR PÅ (Blå)
        validMove[toRow][toCol] = "R";     // Visar rutan pjäsen SKA TILL (Röd)

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.println("AI paus avbruten: " + e.getMessage());
        }

        Piece movingPiece = board[fromRow][fromCol];
        movingPiece.setMoved();

        // ----------------------------------------------------
        // 1. KONTROLLERA OM AI:N GÖR ROCKAD
        // ----------------------------------------------------
        // Om Kungen hoppar 2 steg i sidled vet vi att det är en rockad.
        if (movingPiece.getPiece() == PieceType.KUNG && Math.abs(toCol - fromCol) == 2) {
            if (toCol > fromCol) {
                // Kort rockad (mot h-linjen)
                Piece rook = board[fromRow][7];
                board[fromRow][5] = rook;       // Flytta tornet in över kungen
                board[fromRow][7] = emptySpace; // Töm gamla torn-rutan
                rook.setMoved();
            } else {
                // Lång rockad (mot a-linjen)
                Piece rook = board[fromRow][0];
                board[fromRow][3] = rook;       // Flytta tornet in över kungen
                board[fromRow][0] = emptySpace; // Töm gamla torn-rutan
                rook.setMoved();
            }
        }

        // ----------------------------------------------------
        // 2. KONTROLLERA OM AI:N GÖR EN PASSANT
        // ----------------------------------------------------
        // Om en bonde går snett (byter kolumn) MEN landar på en tom ruta = En Passant!
        if (movingPiece.getPiece() == PieceType.BONDE && fromCol != toCol && board[toRow][toCol].getPiece() == PieceType.NONE) {
            board[fromRow][toCol] = emptySpace; // Ta bort bonden den "hoppade över"
        }

        // Genomför själva standard-draget som AI:n bad om
        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = emptySpace;

        selectedRow = -1;
        selectedCol = -1;

        endTurn();
    }        private void clearEnPassant () {
            enPassantRow = -1;
            enPassantCol = -1;

            enPassantOwner = Player.NONE;
        }

        private void registerEnPassant ( int startRow, int startCol, int endRow, int endCol){
            if (board[endRow][endCol].getPiece() != PieceType.BONDE) {
                return;
            }


            if (Math.abs(endRow - startRow) == 2) {
                enPassantRow = (startRow + endRow) / 2;
                enPassantCol = endCol;
                enPassantOwner = board[endRow][endCol].getOwner();
            }
        }

        private boolean isEnPassant ( int startRow, int startCol, int endRow, int endCol){
            if (board[startRow][startCol].getPiece() != PieceType.BONDE) {
                return false;
            }

            return endRow == enPassantRow && endCol == enPassantCol;
        }

        private void executeEnPassant(int toRow, int toCol) {
            int capturedPawnRow =
                    currentPlayer == Player.WHITE ? toRow - 1 : toRow + 1;
            board[capturedPawnRow][toCol] = emptySpace;
        }

        private void markEnPassantIfValid ( int row, int col, int direction){
            if (enPassantOwner == Player.NONE || enPassantOwner == currentPlayer) {
                return;
            }

            if (col - 1 >= 0 &&
                    row + direction == enPassantRow &&
                    col - 1 == enPassantCol) {
                validMove[row + direction][col - 1] = "R";
            }

            if (col + 1 < 8 &&
                    row + direction == enPassantRow &&
                    col + 1 == enPassantCol) {
                validMove[row + direction][col + 1] = "R";
            }
        }

    public Piece bondeChangesPiece(int selectedRow, int selectedCol)
    {
        System.out.println("Gul: Torn, Lila: Löpare, Orange: Häst, Rosa: Drottning");

        if (validMove[selectedRow][selectedCol] != null && validMove[selectedRow][selectedCol].equals("Y"))
        {
            return new Piece(currentPlayer, PieceType.TORN);
        }
        else if (validMove[selectedRow][selectedCol] != null && validMove[selectedRow][selectedCol].equals("L"))
        {
            return new Piece(currentPlayer, PieceType.LÖPARE);
        }
        else if (validMove[selectedRow][selectedCol] != null && validMove[selectedRow][selectedCol].equals("O"))
        {
            return new Piece(currentPlayer, PieceType.HÄST);
        }
        else if (validMove[selectedRow][selectedCol] != null && validMove[selectedRow][selectedCol].equals("P"))
        {
            return new Piece(currentPlayer, PieceType.DROTTNING);
        }
        return null;
    }

    public void markChangeBondeValid()
    {
        validMove[3][2] = "L";
        validMove[4][2] = "L";
        validMove[3][3] = "Y";
        validMove[4][3] = "Y";
        validMove[3][4] = "O";
        validMove[4][4] = "O";
        validMove[3][5] = "P";
        validMove[4][5] = "P";
    }
}