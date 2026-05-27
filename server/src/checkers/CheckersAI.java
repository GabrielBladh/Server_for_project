package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hanterar logiken för datormotståndaren (AI) i spelet dam.
 * Beräknar och utför det bästa draget baserat på ett inbyggt poängsystem.
 *
 * @author Ali Sojod
 * @date 2026-04-27
 */
public class CheckersAI {

    /**
     * En intern hjälpklass som representerar ett enskilt drag som AI:n kan göra.
     * Lagrar startposition, slutposition, om draget är ett hopp, samt en totalpoäng.
     */
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

    /**
     * Räknar ut och utför nästa drag åt datorspelaren i en separat tråd
     * (för att simulera "tänktid" och undvika att frysa servern/spelet).
     * * @param game Instansen av spelet för att hämta brädet och utföra klick.
     */
    public static void doComputerMove(Checkers game) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("AI blev avbruten.");
            }

            if (!game.currentPlayer.equals("R") || game.isGameEnded) {
                return;
            }

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
                return;
            }

            String[][] tempBoard = game.board;
            List<Move> possibleMoves = new ArrayList<>();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    String piece = tempBoard[r][c];

                    if (piece != null && (piece.equals("R") || piece.equals("D"))) {

                        game.checkMoves(r, c, piece, false);

                        for (int gr = 0; gr < 8; gr++) {
                            for (int gc = 0; gc < 8; gc++) {
                                if ("G".equals(tempBoard[gr][gc])) {
                                    boolean isJump = Math.abs(r - gr) == 2;
                                    Move move = new Move(r, c, gr, gc, isJump);

                                    move.score = calculateMoveScore(move, piece);
                                    possibleMoves.add(move);

                                    tempBoard[gr][gc] = null;
                                }
                            }
                        }
                    }
                }
            }

            if (possibleMoves.isEmpty()) {
                return;
            }

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

            if (game.currentPlayer.equals("R")) {
                System.out.println("AI väljer pjäs på: " + chosenMove.startRow + ":" + chosenMove.startCol);
                boolean success = game.placeTile(chosenMove.startRow, chosenMove.startCol);

                if (success) {
                    System.out.println("AI flyttar till: " + chosenMove.endRow + ":" + chosenMove.endCol);
                    game.placeTile(chosenMove.endRow, chosenMove.endCol);
                }
            }

        }).start();
    }

    /**
     * Analyserar hur bra ett specifikt drag är genom att belöna fördelar
     * som att hoppa, nå kanten, eller flytta framåt mot en kröning.
     * * @param move Draget som undersöks.
     * @param piece Pjäsen som ska utföra draget.
     * @return En siffra där ett högre tal betyder ett mycket bättre drag.
     */
    private static int calculateMoveScore(Move move, String piece) {
        int score = 0;

        if (move.isJump) {
            score += 1000;
        }

        if (piece.equals("R") && move.endRow == 0) {
            score += 500;
        }

        if (move.endCol == 0 || move.endCol == 7) {
            score += 50;
        }

        if (piece.equals("R")) {
            score += (7 - move.endRow) * 10;
        }

        if (move.endCol >= 2 && move.endCol <= 5) {
            score += 20;
        }

        return score;
    }
}