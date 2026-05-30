/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Leaderboard;
import chessgame.shared.Player;

/**
 *
 * @author arvid.renestam
 */
public final class ClientLeaderboard {
    
    public static final int SIDE_PADDING = 15;
    public static final int WIDTH = 300;
    
    private final Leaderboard leaderboard;

    public ClientLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }
    
    public Leaderboard getLeaderboard() { return this.leaderboard; }
    
    // the text of the top right panel, the players with highest score
    public String getLeaderboardText() {
        String text = "";
        
        Player[] topPlayers = leaderboard.getTopPlayers();
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
        for (int i = leaderboard.getMatches().size() - 1; i >= 0; i--) {
            text += leaderboard.getMatches().get(i).toString() + "\n";
        }
        
        return text;
    }
    
}
