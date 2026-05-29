package chess;
import Game.Game;
import Save.SaveSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Huvudklassen för Schackspelet. Hanterar spelregler, brädets uppdateringar,
 * och synkronisering mellan den fysiska spelaren och AI-motorn.
 * * @author Ali Sojod & Gabriel Bladh & Matilda Råstam
 */

public class Chess implements Game {
    private PieceType aiPromotionPiece = PieceType.NONE;
    private Player aiColor = Player.BLACK;
    private String difficultyLevel = "easy";
    private boolean AIgame = false;
    private StockfishEngine engine;
    private Player currentPlayer = Player.WHITE;
    private static final Piece emptySpace = new Piece(Player.NONE, PieceType.NONE);
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int enPassantRow = -1;
    private int enPassantCol = -1;
    private Player enPassantOwner = Player.NONE;
    private String[][] validMove = new String[8][8];
    private boolean promotionMode = false;
    private int promotionRow = -1;
    private int promotionCol = -1;
    private boolean isGameEnded = false;
    private int aiPendingFromRow = -1;
    private int aiPendingFromCol = -1;
    private int aiPendingToRow = -1;
    private int aiPendingToCol = -1;
    String[][] boardMapping = {
            {"H1","G1","F1","E1","D1","C1","B1","A1"},
            {"H2","G2","F2","E2","D2","C2","B2","A2"},
            {"H3","G3","F3","E3","D3","C3","B3","A3"},
            {"H4","G4","F4","E4","D4","C4","B4","A4"},
            {"H5","G5","F5","E5","D5","C5","B5","A5"},
            {"H6","G6","F6","E6","D6","C6","B6","A6"},
            {"H7","G7","F7","E7","D7","C7","B7","A7"},
            {"H8","G8","F8","E8","D8","C8","B8","A8"}
    };

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
    /**
     * Startar spelet och placerar ut alla pjäser på deras standardpositioner.
     * * @author Gabriel Bladh
     */
    public void StartGame() {
        board[0][0] = new Piece(Player.WHITE, PieceType.TORN);
        board[0][1] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][2] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][3] = new Piece(Player.WHITE, PieceType.KUNG);
        board[0][4] = new Piece(Player.WHITE, PieceType.DROTTNING);
        board[0][5] = new Piece(Player.WHITE, PieceType.LÖPARE);
        board[0][6] = new Piece(Player.WHITE, PieceType.HÄST);
        board[0][7] = new Piece(Player.WHITE, PieceType.TORN);
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Piece(Player.WHITE, PieceType.BONDE);
        }

        board[7][0] = new Piece(Player.BLACK, PieceType.TORN);
        board[7][1] = new Piece(Player.BLACK, PieceType.HÄST);
        board[7][2] = new Piece(Player.BLACK, PieceType.LÖPARE);
        board[7][3] = new Piece(Player.BLACK, PieceType.KUNG);
        board[7][4] = new Piece(Player.BLACK, PieceType.DROTTNING);
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

    @Override
    public void saveGame(Game game) throws IOException {
        SaveSystem.saveGame(game);
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

    /**
     * Försöker placera eller markera en pjäs utifrån koordinater från Android-appen.
     * Hanterar även AI:ns drag och bondeförvandling.
     * * @return true om trycket var giltigt och hanterades, annars false.
     * @author Gabriel Bladh
     */
    @Override
    public boolean placeTile(int row, int col) throws IOException {
        if (!isGameEnded) {
            if (isAITurn()) {
                if (aiPendingFromRow != -1) { // AI väntar på att du flyttar dess pjäs
                    if (selectedRow == -1 && selectedCol == -1) {
                        // Första trycket MÅSTE vara på pjäsen AI vill flytta (Blå ruta)
                        if (row == aiPendingFromRow && col == aiPendingFromCol) {
                            selectedRow = row;
                            selectedCol = col;
                            return true;
                        }
                    } else {
                        // --- NYTT SKYDD: Ignorera om man råkar dubbelklicka på startrutan ---
                        if (row == selectedRow && col == selectedCol) {
                            return true;
                        }
                        // --------------------------------------------------------------------

                        // Andra trycket MÅSTE vara på rutan AI vill flytta till (Röd ruta)
                        if (row == aiPendingToRow && col == aiPendingToCol) {
                            placeTileAI(aiPendingFromRow, aiPendingFromCol, aiPendingToRow, aiPendingToCol);

                            // Nollställ minnet inför nästa runda
                            aiPendingFromRow = -1; aiPendingFromCol = -1;
                            aiPendingToRow = -1;   aiPendingToCol = -1;
                            return true;
                        } else {
                            // Felklick, avbryt och tvinga människan att försöka igen
                            selectedRow = -1;
                            selectedCol = -1;
                            return false;
                        }
                    }
                }
                return false; // Ignorera alla andra knapptryck tills AI sagt sitt drag
            }

            if (promotionMode) {
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

            if (!isEmpty(row, col) && board[row][col].getOwner().equals(currentPlayer))
            {
                checkIfKingChecked(currentPlayer);

                clearValidMoves();
                selectedRow = row;
                selectedCol = col;

                markBlue(selectedRow, selectedCol);
                checkMoves(row, col);
                return true;
            }
            if (validMove[row][col] == null) {
                if (selectedRow != -1) {
                    clearValidMoves();
                    selectedRow = -1;
                    selectedCol = -1;
                }
                return false;
            }

            if (selectedRow != -1 && selectedCol != -1 &&
                    (validMove[row][col].equals("G") || validMove[row][col].equals("R")))
            {

                makeMove(row, col);
                if (!promotionMode)
                {
                    endTurn();
                }
                saveGame(this);
                return true;
            }
            return false;
        }
        return false;
    }

    public void makeMove(int row, int col) {
        board[selectedRow][selectedCol].setMoved();
        int fromRow = selectedRow;
        int fromCol = selectedCol;

        if (isEnPassant(fromRow, fromCol, row, col)) {
            executeEnPassant(row, col);
        }

        Piece movingPiece = board[fromRow][fromCol];
        if (movingPiece.getPiece() == PieceType.KUNG && Math.abs(col - selectedCol) == 2 ||
                movingPiece.getPiece() == PieceType.KUNG && Math.abs(col - selectedCol) == 3) {
            if (col > selectedCol) {
                Piece rook = board[selectedRow][selectedCol + 4];
                board[selectedRow][selectedCol + 1] = rook;
                board[selectedRow][selectedCol + 4] = emptySpace;
                rook.setMoved();
            } else {
                Piece rook = board[selectedRow][selectedCol - 3];
                board[selectedRow][selectedCol - 1] = rook;
                board[selectedRow][selectedCol - 3] = emptySpace;
                rook.setMoved();
            }
        }

        board[row][col] = board[selectedRow][selectedCol];
        board[fromRow][fromCol] = emptySpace;
        if (board[row][col].getPiece() == PieceType.BONDE && (row == 0 || row == 7)) {

            if (isAITurn()) {
                PieceType promoType = (aiPromotionPiece != PieceType.NONE) ? aiPromotionPiece : PieceType.DROTTNING;
                board[row][col] = new Piece(aiColor, promoType);
                aiPromotionPiece = PieceType.NONE;
                System.out.println("🤖 AI uppgraderade sin bonde till en " + promoType);
            } else {
                promotionMode = true;
                promotionRow = row;
                promotionCol = col;
                markChangeBondeValid();
            }
        }
        clearEnPassant();
        registerEnPassant(fromRow, fromCol, row, col);

        selectedRow = -1;
        selectedCol = -1;
        clearValidMoves();
    }
    /**
     * Vår egenutvecklade "Greedy Random"-AI för Easy-läget.
     * Letar igenom brädet och prioriterar att slå en pjäs, annars drar den slumpmässigt.
     * * @author Ali Sojod
     */
    /**
     * Vår egenutvecklade AI för Easy och Joakim-läget.
     * @author Ali Sojod
     */
    private void doBeginnerMove() {
        new Thread(() -> {
            try {
                // Tvingar AI:n att "tänka" för att skapa en realistisk fördröjning
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("AI blev avbruten.");
            }

            // SÄKERHETSSPÄRR: Kolla att det fortfarande är AI:ns tur
            if (!isAITurn() || isGameEnded) {
                return;
            }

            List<int[]> possibleMoves = new ArrayList<>();
            List<int[]> captureMoves = new ArrayList<>();

            // 1. Skanna hela brädet efter AI:ns egna pjäser
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c].getOwner() == aiColor) {

                        // 2. Kolla alla drag för denna pjäs
                        clearValidMoves();
                        checkMoves(r, c);

                        // 3. Dela upp dragen i vanliga drag (G) och attack-drag (R)
                        for (int tr = 0; tr < 8; tr++) {
                            for (int tc = 0; tc < 8; tc++) {
                                if ("G".equals(validMove[tr][tc])) {
                                    possibleMoves.add(new int[]{r, c, tr, tc});
                                } else if ("R".equals(validMove[tr][tc])) {
                                    captureMoves.add(new int[]{r, c, tr, tc});
                                }
                            }
                        }
                    }
                }
            }

            // Rensa brädet från sökningen
            clearValidMoves();

            // Om AI:n är schackmatt eller instängd
            if (possibleMoves.isEmpty() && captureMoves.isEmpty()) {
                System.out.println("AI hittar inga drag. Människan vinner!");
                isGameEnded = true;
                return;
            }

            // 4. BESLUTSFASEN (Joakim vs Easy)
            int[] chosenMove = null;
            Random rand = new Random();

            if (difficultyLevel.equals("joakim")) {
                // JOAKIM MODE: Tar bara pjäser om han är absolut tvungen
                if (!possibleMoves.isEmpty()) {
                    chosenMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
                } else {
                    chosenMove = captureMoves.get(rand.nextInt(captureMoves.size()));
                }

            } else {
                // EASY MODE: 60% chans att anfalla, 40% chans att ta det lugnt
                int attackChance = rand.nextInt(100);

                if (!captureMoves.isEmpty() && attackChance < 60) {
                    System.out.println("Easy AI väljer att attackera!");
                    chosenMove = captureMoves.get(rand.nextInt(captureMoves.size()));
                } else if (!possibleMoves.isEmpty()) {
                    System.out.println("Easy AI tar det lugnt och gör ett vanligt drag.");
                    chosenMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
                } else {
                    chosenMove = captureMoves.get(rand.nextInt(captureMoves.size()));
                }
            }

            // 5. SPARA OCH VERKSTÄLL DRAGET FÖR LED-BRÄDET
            if (chosenMove != null) {
                aiPendingFromRow = chosenMove[0];
                aiPendingFromCol = chosenMove[1];
                aiPendingToRow = chosenMove[2];
                aiPendingToCol = chosenMove[3];
                if (board[aiPendingFromRow][aiPendingFromCol].getPiece() == PieceType.BONDE) {
                    if (aiPendingToRow == 0 || aiPendingToRow == 7) {
                        aiPromotionPiece = PieceType.DROTTNING;
                    }
                } else {
                    aiPromotionPiece = PieceType.NONE;
                }

                // Färgkoder för LED-listerna (B = Blå start, R = Röd mål)
                validMove[aiPendingFromRow][aiPendingFromCol] = "B";
                validMove[aiPendingToRow][aiPendingToCol] = "R";

                System.out.println("AI (" + difficultyLevel + ") förbereder drag från " +
                        aiPendingFromRow + ":" + aiPendingFromCol + " till " +
                        aiPendingToRow + ":" + aiPendingToCol);
            }

        }).start();
    }    /**
     * Beräknar var en pjäs får gå enligt schackreglerna.
     * * @author Gabriel Bladh
     */
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

                    Piece target = this.board[newRow][newCol];
                    if (target != null && target.getOwner().equals(currentPlayer) || !wouldLeaveKingInCheck(row, col, newRow, newCol) && !markIfValidSliding(newRow, newCol) || target != null && target.getPiece() != PieceType.NONE) {
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

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                        break;
                    }

                    Piece target = this.board[newRow][newCol];
                    if (target != null && target.getOwner().equals(currentPlayer) || !wouldLeaveKingInCheck(row, col, newRow, newCol) && !markIfValidSliding(newRow, newCol) || target != null && target.getPiece() != PieceType.NONE) {
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

                    Piece target = this.board[newRow][newCol];
                    if (target != null && target.getOwner().equals(currentPlayer) || !wouldLeaveKingInCheck(row, col, newRow, newCol) && !markIfValidSliding(newRow, newCol) || target != null && target.getPiece() != PieceType.NONE) {
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

                Player enemyPlayer = (currentPlayer == Player.WHITE)
                        ? Player.BLACK
                        : Player.WHITE;
                if (!wouldLeaveKingInCheck(row, col, newRow, newCol))
                {
                    markIfValid(newRow, newCol);
                }
                if (!board[row][col].getisMoved() &&
                        !board[row][col - 3].getisMoved() &&
                        board[row][col - 3].getPiece().equals(PieceType.TORN) &&
                        isEmpty(row, col - 2) &&
                        isEmpty(row, col - 1) &&
                        !isSquareAttacked(row, col -2, enemyPlayer) &&
                        !isSquareAttacked(row, col - 1, enemyPlayer) &&
                        !checkIfKingChecked(currentPlayer))
                {
                    int newCol2 = col - 2;
                    if (!wouldLeaveKingInCheck(row, col, row, newCol2))
                    {
                        markIfValid(row, newCol2);
                    }
                }
                if (!board[row][col].getisMoved() &&
                        !board[row][col + 4].getisMoved() &&
                        board[row][col + 4].getPiece().equals(PieceType.TORN) &&
                        isEmpty(row, col + 1) &&
                        isEmpty(row, col + 2) &&
                        isEmpty(row, col + 3) &&
                        !isSquareAttacked(row, col + 1, enemyPlayer) &&
                        !isSquareAttacked(row, col + 2, enemyPlayer) &&
                        !isSquareAttacked(row, col + 3, enemyPlayer) &&
                        !checkIfKingChecked(currentPlayer))
                {
                    int newCol2 = col + 2;
                    if (!wouldLeaveKingInCheck(row, col, row, newCol2))
                    {
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
        if (checkIfCheckMate())
        {
            setIsGameEnded();
        }
    }

    public boolean isAITurn() {
        return AIgame && currentPlayer == aiColor;
    }
    public void setAiColor(Player color){
        this.aiColor = color;
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
                if (piece != null && piece.getPiece() != PieceType.NONE)
                {
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

    public boolean checkIfCheckMate()
    {
        if (!checkIfKingChecked(currentPlayer))
        {
            return false;
        }

        for (int fromRow = 0; fromRow < 8; fromRow++)
        {
            for (int fromCol = 0; fromCol < 8; fromCol++)
            {
                if (isEmpty(fromRow, fromCol))
                {
                    continue;
                }

                Piece piece = board[fromRow][fromCol];

                if (!piece.getOwner().equals(currentPlayer))
                {
                    continue;
                }

                for (int toRow = 0; toRow < 8; toRow++)
                {
                    for (int toCol = 0; toCol < 8; toCol++)
                    {
                        // move itself -> itself
                        if (fromRow == toRow && fromCol == toCol)
                        {
                            continue;
                        }

                        if (!canMove(fromRow, fromCol, toRow, toCol))
                        {
                            continue;
                        }

                        if (!wouldLeaveKingInCheck(fromRow, fromCol, toRow, toCol))
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean canMove(int r, int c, int tr, int tc)
    {
        // board bounds
        if (tr < 0 || tr >= 8 || tc < 0 || tc >= 8)
        {
            return false;
        }

        // same square
        if (r == tr && c == tc)
        {
            return false;
        }

        Piece piece = board[r][c];

        if (piece == null || piece.getPiece() == PieceType.NONE)
        {
            return false;
        }

        Piece target = board[tr][tc];

        // cannot capture own piece
        if (!isEmpty(tr,tc)
                && target.getOwner() == piece.getOwner())
        {
            return false;
        }

        int dr = tr-r;
        int dc = tc-c;

        switch(piece.getPiece())
        {

            case HÄST:

                dr = Math.abs(dr);
                dc = Math.abs(dc);

                return (dr==2 && dc==1)
                        || (dr==1 && dc==2);


            case TORN:

                if (r!=tr && c!=tc)
                {
                    return false;
                }

                return isPathClear(r,c,tr,tc);


            case LÖPARE:

                if (Math.abs(dr)!=Math.abs(dc))
                {
                    return false;
                }

                return isPathClear(r,c,tr,tc);


            case DROTTNING:

                boolean straight =
                        r==tr || c==tc;

                boolean diagonal =
                        Math.abs(dr)==Math.abs(dc);

                if (!straight && !diagonal)
                {
                    return false;
                }

                return isPathClear(r,c,tr,tc);


            case KUNG:

                // normal king move
                if (Math.abs(dr)<=1
                        && Math.abs(dc)<=1)
                {
                    return true;
                }

                // kingside castle
                if (!piece.getisMoved()
                        && tr==r
                        && tc==c+2
                        && isEmpty(r,c+1)
                        && isEmpty(r,c+2)
                        && board[r][7].getPiece()==PieceType.TORN
                        && !board[r][7].getisMoved())
                {
                    return true;
                }

                // queenside castle
                if (!piece.getisMoved()
                        && tr==r
                        && tc==c-2
                        && isEmpty(r,c-1)
                        && isEmpty(r,c-2)
                        && isEmpty(r,c-3)
                        && board[r][0].getPiece()==PieceType.TORN
                        && !board[r][0].getisMoved())
                {
                    return true;
                }

                return false;


            case BONDE:

                if (piece.getOwner()==Player.WHITE)
                {
                    // move forward
                    if (tc==c && tr==r+1
                            && isEmpty(tr,tc))
                    {
                        return true;
                    }

                    // first double move
                    if (!piece.getisMoved()
                            && tc==c
                            && tr==r+2
                            && isEmpty(r+1,c)
                            && isEmpty(r+2,c))
                    {
                        return true;
                    }

                    // capture
                    if (tr==r+1
                            && Math.abs(tc-c)==1
                            && !isEmpty(tr,tc))
                    {
                        return true;
                    }
                }

                else
                {
                    if (tc==c && tr==r-1
                            && isEmpty(tr,tc))
                    {
                        return true;
                    }

                    if (!piece.getisMoved()
                            && tc==c
                            && tr==r-2
                            && isEmpty(r-1,c)
                            && isEmpty(r-2,c))
                    {
                        return true;
                    }

                    if (tr==r-1
                            && Math.abs(tc-c)==1
                            && !isEmpty(tr,tc))
                    {
                        return true;
                    }
                }

                return false;
        }

        return false;
    }

    public boolean isPathClear(int r,int c,int tr,int tc)
    {
        int dRow =
                Integer.signum(tr-r);

        int dCol =
                Integer.signum(tc-c);

        int currentRow=r+dRow;
        int currentCol=c+dCol;

        while(currentRow!=tr
                || currentCol!=tc)
        {
            if(!isEmpty(currentRow,currentCol))
            {
                return false;
            }

            currentRow+=dRow;
            currentCol+=dCol;
        }

        return true;
    }

    public void stopAI() {
        if (AIgame && engine != null)
        {
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

    public void setIsGameEnded()
    {
        isGameEnded = true;
        Player enemyPlayer = (currentPlayer == Player.WHITE)
                ? Player.BLACK
                : Player.WHITE;
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
        if (enemyPlayer == Player.WHITE)
        {
            for (int row = 0; row < 2; row++)
            {
                for (int col = 0; col < validMove.length; col++)
                {
                    validMove[row][col] = "G";
                    validMove[kingPositionRow][kingPositionCol] = "R";
                }
            }
        }
        else
        {
            for (int row = 6; row < 8; row++)
            {
                for (int col = 0; col < validMove.length; col++)
                {
                    validMove[row][col] = "G";
                    validMove[kingPositionRow][kingPositionCol] = "R";
                }
            }
        }
    }

    private boolean isEmpty(int row, int col) {
        return board[row][col].getPiece() == PieceType.NONE;
    }
    /**
     * Hämtar brädets aktuella tillstånd och bygger en sträng enligt FEN-notation,
     * vilket krävs för att den externa schackmotorn ska förstå spelet.
     * * @author Ali Sojod
     */

    public String getFEN() {
        StringBuilder fen = new StringBuilder();

        // 1. Läs av brädet (Från rad 7 ner till rad 0)
        for (int row = 7; row >= 0; row--) {
            int emptySquares = 0;
            for (int col = 7; col >= 0; col--) {
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

        String rockadMöjligt = "";

        if (board[0][3].getPiece() == PieceType.KUNG && !board[0][3].getisMoved()) {

            if (board[0][0].getPiece() == PieceType.TORN && !board[0][0].getisMoved()) {
                rockadMöjligt += "Q";
            }

            if (board[0][7].getPiece() == PieceType.TORN && !board[0][7].getisMoved()) {
                rockadMöjligt += "K";
            }
        }

        if (board[7][3].getPiece() == PieceType.KUNG && !board[7][3].getisMoved()) {

            if (board[7][0].getPiece() == PieceType.TORN && !board[7][0].getisMoved()) {
                rockadMöjligt += "q";
            }


            if (board[7][7].getPiece() == PieceType.TORN && !board[7][7].getisMoved()) {
                rockadMöjligt += "k";
            }
        }

        if (rockadMöjligt.isEmpty()) {
            rockadMöjligt = "-";
        }

        fen.append(rockadMöjligt).append(" ");

        fen.append("- ");

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
    /**
     * Ansvarar för att aktivera Stockfish-AI:n och ställa in svårighetsgrad.
     * * @author Ali Sojod
     */
    public void setAI(boolean isAI) {
        this.AIgame = isAI;
        if (isAI) {
            engine = new StockfishEngine();
            String macPath = "../../stockfish/stockfish-macos-m1-apple-silicon";
            engine.startEngine(macPath);
            if(!difficultyLevel.equals("easy")){
                engine.setDifficulty(difficultyLevel);
            }
            if (isAITurn()){
                doComputerMove();
            }
        }
    }

    public void setDifficultyLevel(String level){
        this.difficultyLevel = level.toLowerCase().trim();
        if (engine != null){
            engine.setDifficulty(this.difficultyLevel);
        }
        System.out.println("Spelets svårighetsgrad har uppdaterats till: " + this.difficultyLevel);
    }
    /**
     * Slår en tärning för att avgöra om datorn ska göra ett smart Stockfish-drag
     * eller ett enklare nybörjardrag (Hybrid-AI). Utför sedan draget i en separat tråd.
     * * @author Ali Sojod
     */
    private void doComputerMove() {
        if (difficultyLevel.equals("easy")){
            doBeginnerMove();
            return;
        }

        if (difficultyLevel.equals("medium")) {
            int chance = (int)(Math.random() * 10) + 1;

            if (chance <= 8) {
                System.out.println("🤖 Medium AI: Spelar som nybörjare (Slump: " + chance + ")");
                doBeginnerMove();
                return;
            }
            else {
                System.out.println("🧠 Medium AI: Blixtrar till och använder Stockfish! (Slump: " + chance + ")");
            }
        }
        new Thread(() -> {
            try {
                String fen = getFEN();
                System.out.println("AI läser brädet som: " + fen);
                System.out.println(getBoardStatus());

                String bestMove = engine.getBestMove(fen);
                System.out.println("Stockfish säger: " + bestMove);
                if (bestMove != null && bestMove.length() == 5) {
                    char promo = bestMove.charAt(4);
                    if (promo == 'q') aiPromotionPiece = PieceType.DROTTNING;
                    else if (promo == 'r') aiPromotionPiece = PieceType.TORN;
                    else if (promo == 'b') aiPromotionPiece = PieceType.LÖPARE;
                    else if (promo == 'n') aiPromotionPiece = PieceType.HÄST;
                } else {
                    aiPromotionPiece = PieceType.NONE;
                }
                if (bestMove == null || bestMove.equals("(none)") || bestMove.equals("Inget drag hittades")) {
                    System.out.println("🏆 SCHACKMATT! Stockfish har inga drag.");
                    setIsGameEnded();
                    return;
                }

                if (bestMove.length() >= 4) {
                    int fromCol = 7 -(bestMove.charAt(0) - 'a');
                    int toCol = 7 - (bestMove.charAt(2) - 'a');
                    int fromRow = Character.getNumericValue(bestMove.charAt(1)) - 1;
                    int toRow = Character.getNumericValue(bestMove.charAt(3)) - 1;

                    aiPendingFromRow = fromRow;
                    aiPendingFromCol = fromCol;
                    aiPendingToRow = toRow;
                    aiPendingToCol = toCol;

                    clearValidMoves();
                    validMove[fromRow][fromCol] = "B"; // Lyser upp startrutan (Blå)
                    validMove[toRow][toCol] = "R";     // Lyser upp målrutan (Röd)
                }

            } catch (Exception e) {
                System.out.println("Fel i AI-tråden: " + e.getMessage());
            }
        }).start();
    }    public void placeTileAI(int fromRow, int fromCol, int toRow, int toCol) {
        if (isGameEnded()) return;

        selectedRow = fromRow;
        selectedCol = fromCol;
        Piece movingPiece = board[fromRow][fromCol];
        movingPiece.setMoved();
        makeMove(toRow, toCol);
        clearValidMoves();
        selectedRow = -1;
        selectedCol = -1;
        endTurn();
    }
    private void clearEnPassant () {
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
    /**
     * Utför regelverket för "En Passant", ett specialdrag där en bonde slår en annan
     * bonde som precis tagit ett dubbelsteg.
     * * @author Gabriel Bladh
     */
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


