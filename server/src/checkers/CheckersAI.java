package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckersAI {

    private static class Move {
        int startRow, startCol;
        int endRow, endCol;
        boolean isJump;

        Move(int sr, int sc, int er, int ec, boolean jump) {
            startRow = sr; startCol = sc; endRow = er; endCol = ec; isJump = jump;
        }
    }

    public static void doComputerMove(Checkers game) {
        // 1. Om AI:n precis gjorde ett drag som kräver ett multijump
        if (game.multiJumpActive) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if ("G".equals(game.board[r][c])) {
                        System.out.println("AI gör ett extra hopp till: " + r + ":" + c);
                        game.placeTile(r, c); // Utför andra halvan av hoppet direkt
                        return;
                    }
                }
            }
        }

        // 2. Normal tur - hitta alla möjliga drag
        String[][] tempBoard = game.board;
        List<Move> possibleMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = tempBoard[r][c];
                if (piece != null && (piece.equals("R") || piece.equals("D"))) {

                    // Kalla på spelets egen logik för att simulera drag ("G" på brädet)
                    game.checkMoves(r, c, piece, false);

                    // Skanna av brädet för att hitta de "G" som just ritades ut
                    for (int gr = 0; gr < 8; gr++) {
                        for (int gc = 0; gc < 8; gc++) {
                            if ("G".equals(tempBoard[gr][gc])) {
                                boolean isJump = Math.abs(r - gr) == 2;
                                possibleMoves.add(new Move(r, c, gr, gc, isJump));
                                tempBoard[gr][gc] = null; // Städa undan "G" omedelbart
                            }
                        }
                    }
                }
            }
        }

        if (possibleMoves.isEmpty()) return; // Spelet är förmodligen slut

        // 3. Välj det bästa draget (hopp har alltid förtur)
        Move chosenMove = null;
        for (Move move : possibleMoves) {
            if (move.isJump) {
                chosenMove = move;
                break; // Vi hittade ett hopp, ta det direkt!
            }
        }

        // Om vi inte kunde hoppa, välj ett slumpmässigt vanligt drag
        if (chosenMove == null) {
            Random rand = new Random();
            chosenMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
        }

        // 4. Utför draget genom att simulera två snabba klick!
        System.out.println("AI väljer pjäs på: " + chosenMove.startRow + ":" + chosenMove.startCol);
        game.placeTile(chosenMove.startRow, chosenMove.startCol);

        System.out.println("AI flyttar till: " + chosenMove.endRow + ":" + chosenMove.endCol);
        game.placeTile(chosenMove.endRow, chosenMove.endCol);
    }
}