package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Hanterar kommunikationen med den externa schackmotorn Stockfish.
 * Översätter spelets tillstånd via UCI-protokollet och hämtar AI:ns bästa drag.
 * * @author Ali Sojod
 */
public class StockfishEngine {

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    private int skillLevel = 10;
    private int moveTime = 1000;
    private boolean useElo = false;
    private int elo = 1320;

    /**
     * Startar Stockfish-processen i bakgrunden.
     * * @param path Filsökvägen till Stockfish-programmet.
     * @return true om motorn startade framgångsrikt, annars false.
     */
    public boolean startEngine(String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder(path);
            engineProcess = pb.start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());

            sendCommand("uci");
            waitFor("uciok");
            applySettings();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Kunde inte starta Stockfish");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Konfigurerar motorns svårighetsgrad och betänketid utifrån vald nivå.
     * * @param level Svårighetsgraden (easy, medium, hard, impossible).
     */
    public void setDifficulty(String level) {
        switch (level.toLowerCase()) {
            case "medium":
                skillLevel = 10;
                useElo = true;
                elo = 2000;
                moveTime = 1500;
                break;
            case "hard":
                skillLevel = 15;
                useElo = true;
                elo = 2500;
                moveTime = 2500;
                break;
            case "impossible":
                skillLevel = 20;
                useElo = false;
                moveTime = 3000;
                break;
            case "easy":
            default:
                skillLevel = 10;
                moveTime = 1000;
                break;
        }
        applySettings();
    }

    /**
     * Skickar de valda inställningarna till Stockfish.
     */
    private void applySettings() {
        sendCommand("setoption name Skill Level value " + skillLevel);
        sendCommand("setoption name UCI_LimitStrength value " + useElo);
        if (useElo) {
            sendCommand("setoption name UCI_Elo value " + elo);
        }
    }

    /**
     * Skickar ett textkommando direkt till Stockfish-terminalen.
     */
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pausar tråden tills Stockfish svarar med en specifik textsträng.
     */
    private void waitFor(String target) {
        try {
            String line;
            while ((line = processReader.readLine()) != null) {
                if (line.contains(target)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Beräknar och returnerar datorns bästa drag utifrån brädets nuvarande utseende.
     * * @param fen Brädets tillstånd i FEN-format.
     * @return Det bästa draget som en sträng (t.ex. "e2e4").
     */
    public String getBestMove(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + moveTime);

        try {
            String line;
            while ((line = processReader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    return line.split(" ")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stänger ner Stockfish-processen säkert för att frigöra minne.
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            if (processReader != null) processReader.close();
            if (processWriter != null) processWriter.close();
            if (engineProcess != null) engineProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}