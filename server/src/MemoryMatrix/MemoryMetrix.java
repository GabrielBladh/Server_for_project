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
    private boolean show;
    private Random random = new Random();
    private ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private int wrongAnswers;
    private boolean endGame;
    int placed = 8;
    int levelPlaced = 8;
    int level = 0;


    public MemoryMetrix() {
        setup(level);
        endGame = false;
    }


    @Override
    public String getGameStatus() {
        if (endGame) {
            return "RNNNNNNRNRNNNNRNNNRNNRNNNNNRRNNNNNRNNRNNNRNNNNRNRNNNNNNR";
        }
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
                else if (boardLight[row][col].equals("B")) {
                    boardStatus.append("B");
                }
                else if (boardLight[row][col].equals("R")) {
                    boardStatus.append("R");
                }
            }
        }
        return boardStatus.toString();
    }

    public void setupBlink(){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (boardBlink[row][col] == null) {
                }
                else if (boardLight[row][col].equals("B")) {
                    boardBlink[row][col] = "1";
                }
            }
        }
    }

    public void setup(int x) {
        show = true;
        int placed = this.placed;
        while (placed > 0) {
            int row = random.nextInt(2-x/2,6+x/2);
            int col = random.nextInt(2-x/2,6+x/2);

            if (boardHidden[row][col] == null) {
                boardHidden[row][col] = "X";
                placed--;
            }
        }
        scheduler.schedule(() -> {
            show = false;
        }, 3, TimeUnit.SECONDS);


    }

    @Override
    public boolean placeTile(int row, int col) {
        if (show || endGame) {
            return true;
        }
        if (boardHidden[row][col] == null) {
            boardHidden[row][col] = "W";
            boardLight[row][col] = "R";
            wrongAnswers++;
        }
        else if (boardHidden[row][col].equals("X")) {
            boardLight[row][col] = "B";
            placed--;
        }
        if (wrongAnswers >= 3){
            endGame();
        }
        checkDone();
        return true;
    }

    @Override
    public String getTurn() {
        return "N";
    }

    public void endGame() {
        endGame = true;

    }

    public void checkDone(){
        if (placed <= 0){
            level++;
            placed = levelPlaced + level*3;
            setupBlink();
            scheduler.schedule(() -> {
                clearBoard();
                setup(level);
            }, 3, TimeUnit.SECONDS);
        }
    }

    public void clearBoard(){
        boardBlink = new String[8][8];
        boardLight = new String[8][8];
        boardHidden = new String[8][8];
    }


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
