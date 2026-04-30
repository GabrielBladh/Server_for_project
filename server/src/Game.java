public interface Game {

    String getGameStatus();
    boolean placeTile(int row, int col);
    String getTurn();
    String getGameEnd();
    boolean isGameEnded();
    String getBoardStatus();
}
