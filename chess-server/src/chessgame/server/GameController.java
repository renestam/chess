/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import chessgame.shared.Match;
import chessgame.shared.Move;

public class GameController {
    
    private final ClientManager whitePlayer;
    private final ClientManager blackPlayer;
    private final Match match;
    // private Board board; // Your server-side board representation to validate rules
    
    public GameController(ClientManager white, ClientManager black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
        
        // Initialize the shared match model
        // (You'd pass real Player objects here)
        this.match = new Match(null, null); 
        
        System.out.println("GameController initialized for a new match!");
        
        // Setup packet routing so this controller handles messages from these two players
        listenToPlayers();
        
        // Send an initial packet to both clients telling them "GAME_START" 
        // and assigning them their colors!
    }
    
    private void listenToPlayers() {
        // Here, you hook into your whitePlayer and blackPlayer ClientManagers 
        // so that when they call 'dealWithIncomingData', it forwards the Move to this controller.
    }

    /**
     * This is the core 'logic' hub of your controller.
     */
    public synchronized void handleIncomingMove(ClientManager sender, Move move) {
        // 1. Check if it's actually this player's turn 
        // (e.g., if sender == whitePlayer but match says it's Black's turn -> ignore!)
        
        // 2. Validate the move using your chess logic rules
        // boolean isValid = board.isMoveLegal(move);
        boolean isValid = true; // Placeholder
        
        if (!isValid) {
            // Send an error packet back *only* to the sender: "Invalid Move!"
            return;
        }
        
        // 3. Apply the move to the server's board state
        // board.makeMove(move);
        match.incrementMovesPlayed();
        
        // 4. Broadcast the approved move to BOTH players so their screens update
        whitePlayer.sendObjectToClient(move);
        blackPlayer.sendObjectToClient(move);
        
        // 5. Check for Checkmate or Stalemate
        // if (board.isCheckmate()) {
        //     match.setWinner(currentTurnColor);
        //     broadcastGameOver();
        // }
    }
}