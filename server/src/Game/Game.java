package Game;

import java.io.IOException;

public interface Game {

    String getGameStatus();
    boolean placeTile(int row, int col) throws IOException;
    String getTurn();
    String getGameEnd();
    boolean isGameEnded();
    String getBoardStatus();

    void saveGame(Game game) throws IOException;
   void setGameStatus(String gameStatus);
   void setBoardStatus(String boardStatus);
   void setTurn(String turn);
   void setGameEnd(String gameEnd);
}
