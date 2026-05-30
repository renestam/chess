/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import chessgame.shared.Leaderboard;
import chessgame.shared.Match;
import chessgame.shared.Player;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author arvid.renestam
 */
public class ServerLeaderboard {
    
    private static final String SAVE_DIR_PATH = "APPDATA/chessgame";
    private static final String PLAYER_FILE_NAME = "players.txt";
    private static final String MATCH_FILE_NAME = "matches.txt";
    
    private final Leaderboard leaderboard;
    
    private final File playerFile;
    private final File matchFile;
    
    public ServerLeaderboard() {
        this.leaderboard = new Leaderboard();
        
        File saveDirectory = new File(SAVE_DIR_PATH);
        this.playerFile = new File(saveDirectory, PLAYER_FILE_NAME);
        this.matchFile = new File(saveDirectory, MATCH_FILE_NAME);
    }
    
    public void addMatch(Match match, boolean isNewMatch) {
        leaderboard.addMatch(match);
        
        if (isNewMatch) {
            updateMatchFile();
            updatePlayerFile();
        }
    }
    
    // load all players and matches and update leaderboard variables
    public void loadFromFiles() {
        // load players
        try (Scanner scanner = new Scanner(playerFile)) {
            while (scanner.hasNextLine()) {
                String name = scanner.nextLine();
                String score = scanner.nextLine();
                leaderboard.getPlayers().add(new Player(name, Float.parseFloat(score)));
            }
        } catch (Exception e) {
            System.err.println("Error loading players from file: " + e);
        }
        
        // load games
        try (Scanner scanner = new Scanner(matchFile)) {
            while (scanner.hasNextLine()) {
                try {
                    String whitePlayerName = scanner.nextLine();
                    String blackPlayerName = scanner.nextLine();
                    String winner = scanner.nextLine();
                    String movesPlayed = scanner.nextLine();
                    addMatch(
                        new Match(
                            leaderboard.getPlayer(whitePlayerName), 
                            leaderboard.getPlayer(blackPlayerName), 
                            winner, 
                            Integer.parseInt(movesPlayed)
                        ),
                        false
                    );
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading games from file: " + e);
        }
    }
    
    // rewrite the file
    private void updatePlayerFile() {
        try (FileWriter fileWriter = new FileWriter(playerFile, false)) {
            for (Player player : leaderboard.getPlayers()) {
                fileWriter.write(player.getName() + "\n");
                fileWriter.write(player.getScore() + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error writing to players file: " + e);
        }
    }
    
    // append a match to the file
    private void updateMatchFile() {
        try (FileWriter fileWriter = new FileWriter(matchFile, false)) {
            for (Match match : leaderboard.getMatches()) {
                fileWriter.write(match.getWhitePlayer().getName() + "\n");
                fileWriter.write(match.getBlackPlayer().getName() + "\n");
                fileWriter.write(match.getWinner() + "\n");
                fileWriter.write(match.getMovesPlayed() + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error writing to matches file: " + e);
        }
    }
    
}
