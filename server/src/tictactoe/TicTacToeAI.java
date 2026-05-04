package tictactoe;

public class TicTacToeAI {

    // Vi sätter Datorn till "R" och Spelaren till "B"
    private static final String COMPUTER = "R";
    private static final String PLAYER = "B";

    /**
     * Huvudmetoden som anropas för att få datorns bästa drag.
     * Returnerar en array med [rad, kolumn].
     */
    public static int[] getBestMove(String[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        // Gå igenom hela brädet för att hitta alla tomma rutor
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                // Är rutan ledig? (I er kod är lediga rutor null)
                if (board[row][col] == null) {
                    // 1. Prova draget temporärt
                    board[row][col] = COMPUTER;

                    // 2. Låt Minimax räkna ut poängen för detta drag
                    int score = minimax(board, 0, false);

                    // 3. Ångra draget direkt (så vi inte förstör brädet)
                    board[row][col] = null;

                    // Om detta drag gav högre poäng än tidigare, spara det!
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }
        return bestMove; // Returnerar t.ex. [1, 1] för mitten
    }

    /**
     * Själva hjärnan (Open Source-magin).
     * Den spelar igenom alla framtida möjliga drag.
     */
    private static int minimax(String[][] board, int depth, boolean isMaximizing) {
        // Kolla om någon har vunnit i denna simulerade framtid
        String result = checkWinner(board);
        if (result != null) {
            if (result.equals(COMPUTER)) return 10 - depth; // Datorn vinner (snabbare vinst ger högre poäng)
            if (result.equals(PLAYER)) return -10 + depth;  // Spelaren vinner (dåligt för datorn)
            if (result.equals("TIE")) return 0;             // Oavgjort
        }

        if (isMaximizing) { // Datorns tur i simuleringen (vill ha högst poäng)
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == null) {
                        board[r][c] = COMPUTER;
                        int score = minimax(board, depth + 1, false);
                        board[r][c] = null;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else { // Spelarens tur i simuleringen (försöker minimera datorns poäng)
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == null) {
                        board[r][c] = PLAYER;
                        int score = minimax(board, depth + 1, true);
                        board[r][c] = null;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    /**
     * En intern hjälpmetod för AI:n för att kolla vem som vunnit.
     * Den liknar er checkEndGame() men returnerar vem som vann istället för att avsluta spelet.
     */
    private static String checkWinner(String[][] board) {
        // Kolla Rader & Kolumner
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]))
                return board[i][0];
            if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]))
                return board[0][i];
        }
        // Kolla Diagonaler
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
            return board[0][0];
        if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]))
            return board[0][2];
        boolean isFull = true;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == null) isFull = false;
            }
        }
        return isFull ? "TIE" : null;
    }

}