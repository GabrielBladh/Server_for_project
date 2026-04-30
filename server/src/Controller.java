public class Controller {

    Game game;
    TCPLisener tcplisener;


    public Controller() {
        Thread thread = new Thread(new TCPLisener(this));
        thread.start();
    }

    public void setGame(Game game) {
        this.game = game;
    }
    public Game getGame() {
        return game;
    }


}
