package checkers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckersAI {

    private static class Move {
        int startRow, startCol;
        int endRow, endCol;
        boolean isJump; // Är detta ett drag där vi äter en pjäs?

        Move(int sr, int sc, int er, int ec, boolean jump) {
            startRow = sr; startCol = sc; endRow = er; endCol = ec; isJump = jump;
        }
    }

    public static void doComputerMove(Checkers game) {
        String[][] tempBoard = game.board;
        List<Move> possibleMoves = new ArrayList<>();

        // 1. Leta igenom brädet efter datorns pjäser ("R" eller "D")
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = tempBoard[r][c];
                if (piece != null && (piece.equals("R") || piece.equals("D"))) {

                    // 2. Vi anropar "checkMoves" för att låta spelet rita ut "G" på brädet
                    // (Observera att vi måste rensa dessa "G" senare om vi inte väljer draget)
                    boolean mustJump = doesPlayerHaveAnyJump(tempBoard, "R");
                    game.checkMoves(r, c, piece, mustJump);

                    // 3. Hitta alla "G" som ritades ut
                    for (int gr = 0; gr < 8; gr++) {
                        for (int gc = 0; gc < 8; gc++) {
                            if ("G".equals(tempBoard[gr][gc])) {
                                // Om avståndet är 2 är det ett hopp!
                                boolean isJump = Math.abs(r - gr) == 2;
                                possibleMoves.add(new Move(r, c, gr, gc, isJump));
                                tempBoard[gr][gc] = null; // Rensa omedelbart "G":et
                            }
                        }
                    }
                }
            }
        }

        // Om vi inte hittar några drag är spelet förmodligen slut
        if (possibleMoves.isEmpty()) return;

        // 4. Välj det BÄSTA draget (Om vi kan hoppa, gör vi det!)
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

        // 5. UTFÖR DRAGET SOM EN MÄNNISKA (Två klick!)
        System.out.println("AI väljer pjäs på: " + chosenMove.startRow + ":" + chosenMove.startCol);
        game.placeTile(chosenMove.startRow, chosenMove.startCol); // Klick 1: Välj pjäs

        System.out.println("AI flyttar till: " + chosenMove.endRow + ":" + chosenMove.endCol);
        game.placeTile(chosenMove.endRow, chosenMove.endCol);     // Klick 2: Flytta
    }

    private static boolean doesPlayerHaveAnyJump(String[][] board, String player) {
        // För enkelhetens skull i AI:n säger vi bara false här om vi bygger en basic bot,
        // men du kan kopiera in er egen doesPlayerHaveAnyJump-logik hit om ni vill ha strikta regler!
        return false;
    }
}
