package SquareJump;
import Game.Game;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SquareJump implements Game {

    private String[][] gameBoard = new String[7][250];
    private int x = 5;
    private int y = 5;
    private ScheduledExecutorService timer = Executors.newScheduledThreadPool(3);;
    private boolean moving = false;


    public SquareJump() {
        World();
        timer.scheduleAtFixedRate(() -> {
            y++;
            System.out.println("tiden går");
            if (!moving && !gameBoard[x+1][y].equals("G")) {
                x++;
                System.out.print("faller");
                System.out.println(x);
            }
            if (gameBoard[x][y].equals("G")) {
                System.out.println("Spelet är slut");
                endGame();
            }

        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void World() {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col <250; col++) {
                if (row < 6) {
                    gameBoard[row][col] = "B";
                }
                else  {
                    gameBoard[row][col] = "G";
                }
            }
        }
        for (int col = 1; col < 249; col++) {
            if (col%20 == 0 &&!(col%40==0)){
                for (int row = col; row < 5 + col; row++) {
                    gameBoard[5][row] = "G";
                }
            }
            if (col%40 == 0){
                for (int row = col; row < 4 + col; row++) {
                    gameBoard[4][row] = "G";
                }
            }
        }
    }

    @Override
    public String getGameStatus() {
        StringBuilder boardStatus = new StringBuilder();
        boardStatus.append("YYBBBBBB");
        for (int row = 1; row < 7; row++) {
            for (int col = y-2; col < y+6; col++) {
                if (col == y && row == x) {
                    boardStatus.append("O");
                }
                else{
                    boardStatus.append(gameBoard[row][col]);
                }
            }
        }
        boardStatus.append("NRNRNNYN");
        return boardStatus.toString();
    }

    @Override
    public boolean placeTile(int row, int col) {
        if (row == 7 && col == 6 && !moving){
            timer.schedule(() -> {
                x--;
            }, 500, TimeUnit.MILLISECONDS);

            timer.schedule(() -> {
                x--;
            }, 1000, TimeUnit.MILLISECONDS);

            timer.schedule(() -> {
                x--;
                moving = false;
            }, 1500, TimeUnit.MILLISECONDS);

            moving = true;
        }

        return true;
    }

    public void endGame(){
        timer.shutdown();
    }

    @Override
    public String getTurn() {
        return "R";
    }
    public void movePiece() {
        gameBoard[x][y] = "O";
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


