/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import chessgame.shared.Board;
import chessgame.shared.Player;
import chessgame.shared.Move;
import chessgame.shared.Match;

/**
 * 
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
        
        this.whiteClient.setGameController(this);
        this.blackClient.setGameController(this);
        
        this.board = new Board();
        
        System.out.println("GameController initialized.");
    }
    
    private ClientManager getOpponent(ClientManager sender) {
        if (sender == whiteClient) return blackClient;
        return whiteClient;
    }
    
    public synchronized void handleIncomingData(ClientManager sender, Object obj) {
        if (obj instanceof Player player) {
            handlePlayerRegistration(sender, player);
        } else if (obj instanceof Move move) {
            handlePlayerMove(sender, move);
        }
    }

    private void handlePlayerRegistration(ClientManager sender, Player player) {
        if (sender == whiteClient) {
            this.whitePlayerProfile = player;
            System.out.println("White player registered: " + player.getName());
        } else if (sender == blackClient) {
            this.blackPlayerProfile = player;
            System.out.println("Black player registered: " + player.getName());
        }
        
        if (whitePlayerProfile != null && blackPlayerProfile != null) {
            System.out.println("Both players ready. Initializing...");
            
            Match matchDetails = new Match(whitePlayerProfile, blackPlayerProfile);
            
            board.startNewMatch(matchDetails, board.whiteToMove());
            
            // send match details to both clients
            whiteClient.sendObjectToClient(matchDetails.setCurrentPlayerIsWhite(true));
            blackClient.sendObjectToClient(matchDetails.setCurrentPlayerIsWhite(false));
        }
    }

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
        
        // validate the move and apply to the official server board
        if (!board.makeMove(move, board.whiteToMove())) {
            System.out.println("Rejected move: Illegal move.");
            return;
        }
        
        getOpponent(sender).sendObjectToClient(move);
    }
}
