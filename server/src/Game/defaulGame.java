package Game;

public class defaulGame implements Game {
    @Override
    public String getGameStatus() {
        return "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
    }

    @Override
    public boolean placeTile(int row, int col) {
        return false;
    }

    @Override
    public String getTurn() {
        return "R";
    }

    @Override
    public String getGameEnd() {
        return "0000000000000000000000000000000000000000000000000000000000000000";
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
