package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckersAI {

    private static class Move {
        int startRow, startCol;
        int endRow, endCol;
        boolean isJump;
        int score;

        Move(int sr, int sc, int er, int ec, boolean jump) {
            startRow = sr; startCol = sc; endRow = er; endCol = ec; isJump = jump;
            score = 0;
        }
    }

    public static void doComputerMove(Checkers game) {
        new Thread(() -> {
            try {
                // Tänketid för att simulera en mänsklig motståndare
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("AI blev avbruten.");
            }

            // SÄKERHETSSPÄRR: Om spelet tagit slut eller människan lyckats stjäla turen, avbryt!
            if (!game.currentPlayer.equals("R") || game.isGameEnded) {
                return;
            }

            // 1. Om AI:n redan är mitt i ett multijump
            if (game.multiJumpActive) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        if ("G".equals(game.board[r][c])) {
                            System.out.println("AI gör ett extra hopp till: " + r + ":" + c);
                            game.placeTile(r, c);
                            return;
                        }
                    }
                }
                return; // Om den inte hittade ett "G" trots att multijump var aktivt, avbryt säkert
            }

            // 2. Normal tur - hitta alla möjliga drag
            String[][] tempBoard = game.board;
            List<Move> possibleMoves = new ArrayList<>();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    String piece = tempBoard[r][c];

                    // Leta endast på datorns pjäser
                    if (piece != null && (piece.equals("R") || piece.equals("D"))) {

                        game.checkMoves(r, c, piece, false);

                        for (int gr = 0; gr < 8; gr++) {
                            for (int gc = 0; gc < 8; gc++) {
                                if ("G".equals(tempBoard[gr][gc])) {
                                    boolean isJump = Math.abs(r - gr) == 2;
                                    Move move = new Move(r, c, gr, gc, isJump);

                                    // Sätt poäng på draget!
                                    move.score = calculateMoveScore(move, piece);
                                    possibleMoves.add(move);

                                    // SÄKER UPPSTÄDNING: Ta bara bort "G", rör aldrig några pjäser
                                    tempBoard[gr][gc] = null;
                                }
                            }
                        }
                    }
                }
            }

            // Om inga drag finns tillgängliga är datorn fast
            if (possibleMoves.isEmpty()) {
                return;
            }

            // 3. Välj det BÄSTA draget baserat på poäng (Hopp prioriteras alltid)
            Move bestMove = possibleMoves.get(0);
            for (Move move : possibleMoves) {
                if (move.score > bestMove.score) {
                    bestMove = move;
                }
            }

            List<Move> topMoves = new ArrayList<>();
            for (Move move : possibleMoves) {
                if (move.score == bestMove.score) {
                    topMoves.add(move);
                }
            }
            Random rand = new Random();
            Move chosenMove = topMoves.get(rand.nextInt(topMoves.size()));

            // 4. Utför draget (och dubbelkolla att det fortfarande är AI:ns tur)
            if (game.currentPlayer.equals("R")) {
                System.out.println("AI väljer pjäs på: " + chosenMove.startRow + ":" + chosenMove.startCol);
                boolean success = game.placeTile(chosenMove.startRow, chosenMove.startCol);

                // Om servern accepterade pjäsen, slutför draget
                if (success) {
                    System.out.println("AI flyttar till: " + chosenMove.endRow + ":" + chosenMove.endCol);
                    game.placeTile(chosenMove.endRow, chosenMove.endCol);
                }
            }

        }).start();
    }

    private static int calculateMoveScore(Move move, String piece) {
        int score = 0;

        // Hopp ger massiv poäng
        if (move.isJump) {
            score += 1000;
        }

        // Att nå slutet och bli dam ger hög poäng
        if (piece.equals("R") && move.endRow == 0) {
            score += 500;
        }

        // Säkra kantsidor
        if (move.endCol == 0 || move.endCol == 7) {
            score += 50;
        }

        // Rör sig framåt
        if (piece.equals("R")) {
            score += (7 - move.endRow) * 10;
        }

        // Kontroll av mitten
        if (move.endCol >= 2 && move.endCol <= 5) {
            score += 20;
        }

        return score;
    }
}