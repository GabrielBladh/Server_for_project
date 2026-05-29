package MemoryMatrix;
import Game.Game;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryMetrix implements Game {

    private String[][] boardBlink = new String[8][8];
    private String[][] boardLight = new String[8][8];
    private String[][] boardHidden = new String[8][8];
    private boolean show = false;
    private Random random = new Random();
    private ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private int wrongAnswers;


    public MemoryMetrix() {
        int placed = 0;

        while (placed < 8) {
            int row = random.nextInt(2,6);
            int col = random.nextInt(2,6);

            if (boardHidden[row][col] == null) {
                boardHidden[row][col] = "X";
                placed++;
            }
        }
        show = true;
        scheduler.schedule(() -> {
            show = false;
        }, 3, TimeUnit.SECONDS);


    }


    @Override
    public String getGameStatus() {
        if (show) {
            StringBuilder boardStatus = new StringBuilder();
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (boardHidden[row][col] == null) {
                        boardStatus.append("N");
                    }
                    else if (boardHidden[row][col].equals("X")) {
                        boardStatus.append("B");
                    }
                    else if (boardHidden[row][col].equals("W")) {
                        boardStatus.append("R");
                    }
                }
            }
            return boardStatus.toString();
        }
        StringBuilder boardStatus = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (boardLight[row][col] == null) {
                    boardStatus.append("N");
                }
                else if (boardHidden[row][col].equals("X")) {
                    boardStatus.append("B");
                }
                else if (boardHidden[row][col].equals("W")) {
                    boardStatus.append("R");
                }
            }
        }
        return boardStatus.toString();
    }

    @Override
    public boolean placeTile(int row, int col) {
        if (show){
            return true;
        }
        if (boardHidden[row][col] == null) {
            boardHidden[row][col] = "W";
            boardLight[row][col] = "R";
            wrongAnswers++;
        }
        else if (boardHidden[row][col].equals("X")) {
            boardLight[row][col] = "B";
        }
        if (wrongAnswers > 3){
            endGame();
        }
        return true;
    }

    @Override
    public String getTurn() {
        return "N";
    }

    public void endGame() {}

    @Override
    public String getGameEnd() {
        return "";
    }

    @Override
    public boolean isGameEnded() {
        return false;
    }

    @Override
    public String getBoardStatus() {
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
}
