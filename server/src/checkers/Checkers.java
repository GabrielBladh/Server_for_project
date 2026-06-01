package checkers;
import Game.Game;

/**
 * Hanterar den centrala spellogiken för dam.
 * Håller koll på spelbrädet, spelarnas turer, giltiga drag och när spelet är slut.
 *
 * @author Ali Sojod
 * @date 2026-04-17
 */
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

    /**
     * Konstruktor som sätter upp startbrädet och nollställer blink-effekterna.
     */
    public Checkers() {
        setupGame();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                blinkBoard[r][c] = "0";
            }
        }
    }

    /**
     * Ställer in om spelet spelas mot datorn (AI) eller en annan människa.
     * * @param AIgame true om motståndaren är AI, annars false.
     */
    public void setAI(boolean AIgame) {
        this.AIgame = AIgame;
    }

    /**
     * Hämtar brädets nuvarande status som en sträng.
     * (Krävs av Game-interfacet)
     * * @return En tom sträng i denna implementation.
     */
    public String getBoardStatus(){
        return "";
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

    /**
     * Avslutar den nuvarande spelarens tur och skickar över turen till motståndaren.
     * Kontrollerar också om nästa spelare har några giltiga drag, annars utses en vinnare.
     * Om motståndaren är AI, utlöses dess drag här.
     */
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

    /**
     * Hämtar vilken spelare som har turen just nu.
     * * @return "B" (Blå) eller "R" (Röd).
     */
    public String getTurn(){
        return currentPlayer;
    }

    /**
     * Hämtar information om vilka rutor som ska blinka när spelet är slut.
     * * @return En sträng som representerar blink-brädet.
     */
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

    /**
     * Kontrollerar om spelet har avslutats.
     * * @return true om någon har vunnit, annars false.
     */
    @Override
    public boolean isGameEnded() {
        return isGameEnded;
    }

    /**
     * Kärnlogiken för att spela! Hanterar när en spelare klickar på brädet.
     * Väljer antingen en pjäs att flytta, eller flyttar en redan vald pjäs.
     * * @param row Raden som klickades på.
     * @param col Kolumnen som klickades på.
     * @return true om åtgärden lyckades eller klicket var giltigt, annars false.
     */
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

    /**
     * Kontrollerar om en pjäs har nått andra sidan brädet och ska krönas till dam (kung).
     * * @param row Nuvarande rad för pjäsen.
     * @param col Nuvarande kolumn för pjäsen.
     * @return true om pjäsen kröntes, annars false.
     */
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

    /**
     * Hittar och markerar alla möjliga rutor som en specifik pjäs kan flytta till.
     * * @param row Pjäsens rad.
     * @param col Pjäsens kolumn.
     * @param piece Pjäsens typ ("B", "R", "M", "D").
     * @param onlyJumps Om true, markeras enbart hopp över motståndare.
     */
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

    /**
     * Går igenom spelarens alla pjäser och kollar om det finns ett "tvingat" hopp någonstans på brädet.
     * * @param player Spelaren att kontrollera ("B" eller "R").
     * @return true om ett hopp finns tillgängligt, annars false.
     */
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

    /**
     * Kontrollerar om en specifik pjäs har möjlighet att hoppa över en motståndare.
     * * @param row Rad för pjäsen.
     * @param col Kolumn för pjäsen.
     * @param piece Vilken sorts pjäs det är.
     * @return true om ett hopp är möjligt, annars false.
     */
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

    /**
     * Hjälpmetod för att se om ett specifikt hopp över en motståndare faktiskt är lagligt.
     * * @param midRow Raden där motståndaren står.
     * @param midCol Kolumnen där motståndaren står.
     * @param endRow Raden man landar på.
     * @param endCol Kolumnen man landar på.
     * @param oppNormal Motståndarens normala pjäs-sträng.
     * @param oppKing Motståndarens dam/kung-sträng.
     * @return true om draget är en laglig tillfångatagning.
     */
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

    /**
     * Kontrollerar om spelaren överhuvudtaget kan göra något giltigt drag på brädet.
     * Används för att avgöra om spelaren har förlorat.
     * * @param player Vilken spelare som ska kontrolleras.
     * @return true om ett drag finns, annars false.
     */
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

    /**
     * Städar upp brädet från "G" (Ghosts) som visar var spelaren kan flytta.
     */
    private void clearValidMoves(){
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++ ){
                if ("G".equals(board[r][c])){
                    board[r][c] = null;
                }
            }
        }
    }

    /**
     * Om en ruta är tom, markeras den med "G" för att visa att det är ett giltigt drag.
     * * @param r Rutan rad.
     * @param c Rutan kolumn.
     */
    private void markIfValid(int r, int c) {
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (board[r][c] == null) {
                board[r][c] = "G";
            }
        }
    }

    /**
     * Håller koll på hur många pjäser varje lag har kvar, och drar av poäng
     * från motståndaren till den som precis gjorde ett drag.
     * * @param currentPlayer Spelaren som precis tog en pjäs.
     */
    private void scoreTracker(String currentPlayer){
        if (currentPlayer.equals("B")){
            redCounter--;
        }
        else {
            blueCounter--;
        }
    }

    /**
     * Om det finns en fiende i mitten och plats bakom, markeras landningsrutan med ett "G".
     */
    private void markCaptureIfValid(int midRow, int midCol, int endRow, int endCol, String oppNormal, String oppKing){
        if (endRow >= 0 && endRow < 8 && endCol >= 0 && endCol < 8){
            String middleSquare = board[midRow][midCol];

            if(middleSquare != null && (middleSquare.equals(oppNormal) || middleSquare.equals(oppKing)) && board[endRow][endCol] == null){
                board[endRow][endCol] = "G";
            }
        }
    }

    /**
     * Avgör om spelet ska ta slut baserat på att ett av lagen förlorat alla pjäser.
     */
    private void checkWinByCounters(){
        if(redCounter == 0){
            setWinner("B");
        }
        else if(blueCounter == 0) {
            setWinner("R");
        }
    }

    /**
     * Sätter vinnaren i spelet och aktiverar en speciell ljus/blink-kombination.
     * * @param winner Vinnarens sträng-ID.
     */
    private void setWinner(String winner) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                blinkBoard[r][c] = "0";
            }
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];

                if (piece != null) {
                    if (winner.equals("B") && (piece.equals("B") || piece.equals("M"))) {
                        blinkBoard[r][c] = "1";
                    }
                    else if (winner.equals("R") && (piece.equals("R") || piece.equals("D"))) {
                        blinkBoard[r][c] = "1";
                    }
                }
            }
        }

        isGameEnded = true;
    }
    /**
     * Konverterar och hämtar brädets layout och vinst-effekter till en enda sträng.
     * Används typiskt av spelservern för att rita upp brädet hos klienten.
     * * @return En sträng av alla rutor.
     */
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
    /**
     * Placerar ut alla startpjäser på rätt positioner för att starta ett nytt parti.
     */
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

    /**
     * Kollar om det är AI:ns tur att spela.
     * * @return true om AI:n är aktiv och röd spelare ("R") har turen.
     */
    public boolean isAITurn() {
        return AIgame && currentPlayer.equals("R");
    }
}