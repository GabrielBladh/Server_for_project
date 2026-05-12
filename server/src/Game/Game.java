package Game;

public interface Game {

    String getGameStatus();
    boolean placeTile(int row, int col);
    String getTurn();
    String getGameEnd();
    boolean isGameEnded();
    String getBoardStatus();

   void setGameStatus(String gameStatus);
   void setBoardStatus(String boardStatus);
   void setTurn(String turn);
   void setGameEnd(String gameEnd);
}
