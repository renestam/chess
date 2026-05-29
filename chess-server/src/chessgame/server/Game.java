/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import chessgame.shared.Match;
import chessgame.shared.Player;

/**
 *
 * @author arvid.renestam
 */
public class Game {
    private final ClientManager whiteClient;
    private final ClientManager blackClient;
    private final Match matchState;

    public Game(ClientManager cm1, ClientManager cm2) {
        this.whiteClient = cm1;
        this.blackClient = cm2;
        
        // 1. Create Player objects (you could fetch actual names over the network later)
        Player white = new Player("Player 1 (White)");
        Player black = new Player("Player 2 (Black)");
        
        // 2. Initialize the shared Match object on the server
        this.matchState = new Match(white, black);
        
        System.out.println("Started: " + matchState.toString());
        
        // Next: Tell both clients the game started and who they are playing against!
        startTrackingTurns();
    }
    
    private void startTrackingTurns() {
        // Code to handle turn logic and update matchState.incrementMovesPlayed()
    }
}
