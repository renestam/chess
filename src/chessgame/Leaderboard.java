/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author arvid.renestam
 */
public final class Leaderboard {
    final private ArrayList<Player> players = new ArrayList<>();
    final private ArrayList<Match> matches = new ArrayList<>();
    
    public static final int SIDE_PADDING = 15;
    public static final int WIDTH = 300;
    
    private static final String APP_DATA = System.getenv("APPDATA");
    private static final File SAVE_DIR = new File(APP_DATA + File.separator + "LurpChessGame");
    private static final File PLAYERS_FILE = new File(SAVE_DIR, "players.txt");
    private static final File MATCHES_FILE = new File(SAVE_DIR, "matches.txt");

    public Leaderboard() {
        SAVE_DIR.mkdirs();
        loadFromFiles();
    }
    
    public void addMatch(Match match, boolean isNewMatch) {
        matches.add(match);
        
        if (isNewMatch) {
            updateMatchFile();
            updatePlayerFile();
        }
    }
    
    public Match getMatch(int index) {
        if (index < matches.size()) {
            return matches.get(index);
        }
        return null;
    }
    
    // sequentially search through players list and return player if present
    private Player searchPlayer(String name) {
        if (players.size() == 0) return null;
        for (Player player : players) {
            if (player.getName().toLowerCase().equals(name.toLowerCase())) {
                return player;
            }
        }
        return null;
    }
    
    public Player getPlayer(String name) {
        // check for existing player
        Player player = searchPlayer(name);
        
        // create new plaeyer
        if (player == null) {
            player = new Player(name, 0);
            players.add(player);
        }
        
        return player;
    }
    
    // get the five players with the highest scores
    public Player[] getTopPlayers() {
        Player[] topPlayers = new Player[5];
        int size = players.size();
        if (size == 0) return topPlayers;
        
        // sort array with insertion sort
        for (int j = 1; j < size; j++) {
            Player temp = players.get(j);
            int i = j - 1;
            while (i >= 0 && players.get(i).getScore() < temp.getScore()) {
                players.set(i + 1, players.get(i));
                i = i - 1;
            }
            players.set(i + 1, temp);
        }
        
        // return the first five elements
        for (int i = 0; i < size; i++) {
            topPlayers[i] = players.get(i);
            if (i >= 4) break;
        }
        
        return topPlayers;
    }
    
    // the text of the top right panel, the players with highest score
    public String getLeaderboardText() {
        String text = "";
        
        Player[] topPlayers = getTopPlayers();
        for (int i = 0; i < topPlayers.length; i++) {
            if (topPlayers[i] == null) break;
            text += (i + 1) + ". " + topPlayers[i].toString();
            if (i < 5 - 1) text += "\n";
        }
        return text;
    }
    
    // the text of the bottom right panel
    public String getMatchesText() {
        String text = "";
        
        // start with the most recent match
        for (int i = matches.size() - 1; i >= 0; i--) {
            text += matches.get(i).toString() + "\n";
        }
        
        return text;
    }
    
    // load all players and matches and update leaderboard variables
    public void loadFromFiles() {
        // load players
        try (Scanner scanner = new Scanner(PLAYERS_FILE)) {
            while (scanner.hasNextLine()) {
                String name = scanner.nextLine();
                String score = scanner.nextLine();
                players.add(new Player(name, Float.parseFloat(score)));
            }
        } catch (Exception e) {
            System.err.println("Error loading players from file: " + e);
        }
        
        // load games
        try (Scanner scanner = new Scanner(MATCHES_FILE)) {
            while (scanner.hasNextLine()) {
                try {
                    String whitePlayerName = scanner.nextLine();
                    String blackPlayerName = scanner.nextLine();
                    String winner = scanner.nextLine();
                    String movesPlayed = scanner.nextLine();
                    addMatch(
                        new Match(
                            getPlayer(whitePlayerName), 
                            getPlayer(blackPlayerName), 
                            winner, 
                            Integer.parseInt(movesPlayed)
                        ),
                        false
                    );
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading games from file: " + e);
        }
    }
    
    // append a player to the file
    private void updatePlayerFile() {
        try (FileWriter fileWriter = new FileWriter(PLAYERS_FILE, false)) {
            for (Player player : players) {
                fileWriter.write(player.getName() + "\n");
                fileWriter.write(player.getScore() + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error writingh to players file: " + e);
        }
    }
    
    // append a match to the file
    private void updateMatchFile() {
        try (FileWriter fileWriter = new FileWriter(MATCHES_FILE, false)) {
            for (Match match : matches) {
                fileWriter.write(match.whitePlayer.getName() + "\n");
                fileWriter.write(match.blackPlayer.getName() + "\n");
                fileWriter.write(match.winner + "\n");
                fileWriter.write(match.getMovesPlayed() + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error writing to matches file: " + e);
        }
    }
    
}
