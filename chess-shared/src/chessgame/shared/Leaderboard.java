/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author arvid
 */
public class Leaderboard implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Match> matches = new ArrayList<>();
    
    public ArrayList<Player> getPlayers() { return this.players; }
    public ArrayList<Match> getMatches() { return this.matches; }
    
    public void addMatch(Match match) { matches.add(match); }
    
    public Match getMatch(int index) {
        if (index < matches.size()) {
            return matches.get(index);
        }
        return null;
    }
    
    // sequentially search through players list and return player if present
    private Player searchPlayer(String name) {
        if (players.isEmpty()) return null;
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
    
}
