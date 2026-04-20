public class Checkers implements Game {

    String[][] board = new String[8][8];
    String currentPlayer = "B";

    // nya variaböer för att kolla vilka rutor, pjäsar man klickar på
    int selectedRow = -1;
    int selectedCol = -1;

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
        // först ska man kolla om rutan man väljer är inte tom för att förhindra NullPointerException och att den tillhär spelaren själv
        if (board[row][col] !=null && board[row][col].equals(currentPlayer)) {
            //släcker andra lampor när man väljer på nytt
            clearValidMoves();

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
        if (player.equals("B")){
            // B går neråt i brädet
            markIfValid(row + 1, col - 1); // ner åt vänster
            markIfValid(row + 1, col + 1); // ner åt höger

            //kolla hoppa över motstånd, isf över R
            markCaptureIfValid(row + 1, col - 1, row + 2, col - 2, "R"); // Hopp ner åt vänster
            markCaptureIfValid(row + 1, col + 1, row + 2, col + 2, "R"); // Hopp ner åt höger
        }
        else if (player.equals("R")){
            // R är motstånd till B och går uppåt på brädet (raden minskar med 1)
            markIfValid(row - 1, col - 1); // upp åt vänster
            markIfValid(row - 1, col + 1); // upp åt höger

            //kolla hopp över motstånd, isf B

            markCaptureIfValid(row - 1, col - 1, row - 2, col - 2, "B"); // hopp upp åt vänster
            markCaptureIfValid(row - 1, col + 1, row - 2, col + 2, "B"); // hopp upp åt höger


        }

    }
    //Metod för att rensa brädet från G (gröna lampor)
    private void clearValidMoves(){

    }

    // metod för att sätta G om rutan är ledig
    private void markIfValid(int r, int c) {
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (board[r][c] == null) {
                board[r][c] = "G";
            }
        }
    }



    // metod för checkMoves för att se om hopp över motstånd är giltig
    /**
    * Om rutan man hoppar över innehåller en motståndarpjäs, och landningsrutan
     * är tom och inom brädets gränser, markeras landningsrutan som ett giltigt drag ("G").
            *
            * @param midRow   Raden för rutan som pjäsen hoppar över (där motståndaren förväntas stå).
            * @param midCol   Kolumnen för rutan som pjäsen hoppar över.
            * @param endRow   Raden för landningsrutan efter hoppet.
            * @param endCol   Kolumnen för landningsrutan efter hoppet.
            * @param opponent Sträng som representerar motståndarens pjäs (t.ex. "R" för Röd, "B" för Svart).
            */
    private void markCaptureIfValid(int midRow, int midCol, int endRow, int endCol, String opponent){
        // kollar om rutan att hoppa över finns på brädet
        if (endRow >= 0 && endRow < 8 && endCol >= 0 && endCol < 8){
            //läser vad som står i mitten rutan (om det är motståndets pjäs)
            String midleSquare = board [midRow][midCol];
            //kollar om det är motstånd i mitten och rutan eften, endRow/endCol är tom
            if(midleSquare != null && midleSquare.equals(opponent) && board[endRow][endCol]==null){
                board[endRow][endCol] = "G";
            }
        }


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
                else if (board[row][col].equals("G")) {
                    boardStatus += "G";
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
