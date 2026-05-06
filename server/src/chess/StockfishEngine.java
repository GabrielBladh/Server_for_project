package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StockfishEngine {

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    // Startar Stockfish i bakgrunden
    public boolean startEngine(String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder(path);
            engineProcess = pb.start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
            return true;
        } catch (Exception e) {
            System.out.println("❌ Kunde inte starta Stockfish! Fel sökväg?");
            e.printStackTrace();
            return false;
        }
    }

    // Skickar ett kommando till Stockfish (t.ex. "uci" eller "isready")
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lyssnar på vad Stockfish svarar
    public String getOutput(int waitTimeMS) {
        StringBuilder output = new StringBuilder();
        try {
            Thread.sleep(waitTimeMS); // Ge Stockfish lite tid att tänka/skriva
            command("isready"); // Tvinga fram en uppdatering
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    output.append(text).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    // Ett snabb-kommando (skickar och väntar på svar)
    public void command(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBestMove(String fen, int waitTimeMS) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTimeMS);

        String output = getOutput(waitTimeMS + 200); // Vänta tiden ut + lite marginal

        // Stockfish svarar med massa text, vi letar efter raden som börjar med "bestmove"
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.startsWith("bestmove")) {
                // Returnerar bara själva draget, t.ex. "e2e4"
                return line.split(" ")[1];
            }
        }
        return "Inget drag hittades";
    }

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
    public static void main(String[] args) {
        StockfishEngine engine = new StockfishEngine();

        String pathToStockfish = "/Users/hooje/Documents/stockfish/stockfish-macos-m1-apple-silicon";

        if (engine.startEngine(pathToStockfish)) {
            System.out.println("✅ Stockfish är igång!");

            // Vi testar startpositionen i Schack
            String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            System.out.println("Låter Stockfish tänka i 1 sekund...");

            String bestMove = engine.getBestMove(startFEN, 1000);
            System.out.println("👑 Bästa draget enligt Stockfish: " + bestMove);

            engine.stopEngine();
        }
    }
}