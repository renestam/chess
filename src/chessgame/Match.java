/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

/**
 *
 * @author arvid.renestam
 */
public final class Match {
    protected final Player whitePlayer, blackPlayer;
    
    protected String winner = ""; // white, black, stalemate
    
    private int movesPlayed = 0;

    public Match(Player firstPlayer, Player secondPlayer) {
        this.whitePlayer = firstPlayer;
        this.blackPlayer = secondPlayer;
    }
    
    public Match(Player firstPlayer, Player secondPlayer, String winner, int movesPlayed) {
        this.whitePlayer = firstPlayer;
        this.blackPlayer = secondPlayer;
        this.movesPlayed = movesPlayed;
        this.winner = winner;
    }
    
    public int getMovesPlayed() {
        return movesPlayed;
    }
    
    public void incrementMovesPlayed() {
        movesPlayed++;
    }
    
    public void setWinner(String newWinner) {
        if (!winner.isBlank()) return;
        winner = newWinner;
        
        // increment players' scores
        if (winner.equals("white") ) {
            whitePlayer.incrementScore();
        } else if (winner.equals("black")) {
            blackPlayer.incrementScore();
        } else if (winner.equals("stalemate")) {
            whitePlayer.incrementScoreStalemate();
            blackPlayer.incrementScoreStalemate();
        }
    }
    
    @Override
    public String toString() {
        String text = "";
        
        text += whitePlayer.getName();
        text += victoryLetter(true);
        
        text += " vs. ";
        
        text += blackPlayer.getName();
        text += victoryLetter(false);
        
        text += ", Moves: " + movesPlayed;
        
        return text;
    }
    
    private String victoryLetter(boolean isWhitePlayer) {
        if (winner.isBlank()) return "";
        if (winner.equals("stalemate")) return " (SM) ";
        if (isWhitePlayer && winner.equals("white")) return " (W)";
        return " (L)"; 
    }
}
