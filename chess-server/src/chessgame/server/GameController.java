package chessgame.server;

import chessgame.shared.Board;
import chessgame.shared.Player;
import chessgame.shared.Move;
import chessgame.shared.Match;

/**
 * The authoritative controller running on the server.
 * Manages game turns, validates moves against the official Board state,
 * and routes packets between opponents.
 * @author arvid.renestam
 */
public class GameController {
    
    private final ClientManager whiteClient;
    private final ClientManager blackClient;
    
    private Player whitePlayerProfile;
    private Player blackPlayerProfile;
    
    private final Board board;

    public GameController(ClientManager cm1, ClientManager cm2) {
        this.whiteClient = cm1;
        this.blackClient = cm2;
        
        // Link this controller instance to both network handlers
        this.whiteClient.setGameController(this);
        this.blackClient.setGameController(this);
        
        this.board = new Board();
        
        System.out.println("GameController initialized and listening to both clients!");
    }
    
    /**
     * Helper method to grab the opposing ClientManager.
     */
    private ClientManager getOpponent(ClientManager sender) {
        if (sender == whiteClient) return blackClient;
        return whiteClient;
    }
    
    /**
     * Central data hub where all deserialized incoming client objects land.
     */
    public synchronized void handleIncomingData(ClientManager sender, Object obj) {
        if (obj instanceof Player player) {
            handlePlayerRegistration(sender, player);
        } else if (obj instanceof Move move) {
            handlePlayerMove(sender, move);
        }
    }

    /**
     * Processes incoming client player profiles and alerts clients when the match begins.
     */
    private void handlePlayerRegistration(ClientManager sender, Player player) {
        if (sender == whiteClient) {
            this.whitePlayerProfile = player;
            System.out.println("White player profile registered: " + player.getName());
        } else if (sender == blackClient) {
            this.blackPlayerProfile = player;
            System.out.println("Black player profile registered: " + player.getName());
        }
        
        // Once BOTH profiles are received, construct the Match context and alert clients
        if (whitePlayerProfile != null && blackPlayerProfile != null) {
            System.out.println("Both profiles ready. Initializing match details broadcast...");
            
            Match matchDetails = new Match(whitePlayerProfile, blackPlayerProfile);
            
            // Send the match details to both clients so they know who they are playing against
            whiteClient.sendObjectToClient(matchDetails.setCurrentPlayerIsWhite(true));
            blackClient.sendObjectToClient(matchDetails.setCurrentPlayerIsWhite(false));
        }
    }

    /**
     * Validates turn compliance, updates server state, and replicates moves to the opponent.
     */
    private void handlePlayerMove(ClientManager sender, Move move) {
        // Turn Enforcement
        if (sender == whiteClient && !board.whiteToMove()) {
            System.out.println("Rejected move: White tried to play out of turn.");
            return;
        }
        if (sender == blackClient && board.whiteToMove()) {
            System.out.println("Rejected move: Black tried to play out of turn.");
            return;
        }

        System.out.println("Received a move attempt: " + move);
        
        // Validate the move and apply to the official Server Board state 
        if (!board.movePiece(move)) {
            System.out.println("Rejected move: Illegal move rules violation.");
            return;
        }
        
        // Broadcast the move to the opponent so their board updates on screen
        getOpponent(sender).sendObjectToClient(move);
    }
}
