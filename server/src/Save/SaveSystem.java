package Save;

import Game.Game;
import chess.Chess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class SaveSystem
{
    public static void saveGame(Game game) throws IOException
    {
        String saveData = "";
        if (game instanceof Chess)
        {
            saveData =  "board=" + game.getBoardStatus() + "\n"
                    + "status=" + game.getGameStatus() + "\n"
                    + "turn=" + game.getTurn();
        }
        else
        {
            saveData = "board=" + game.getGameStatus() + "\n" + "turn=" + game.getTurn() + "\n" + "game end=" + game.getGameEnd();
        }

        Files.writeString(Path.of("save.txt"), saveData);

        System.out.println(saveData);
    }

    public static Game loadGame(Game game) throws IOException
    {
        String data = Files.readString(Path.of("save.txt"));

        String[] lines = data.split("\n");

        if (game instanceof Chess)
        {
            game.setBoardStatus(lines[0]);
            game.setGameStatus(lines[1]);
            game.setTurn(lines[2]);
        }
        else
        {
            game.setGameStatus(lines[0]);
            game.setTurn(lines[1]);
            game.setGameEnd(lines[2]);
        }

        return game;
    }
}
