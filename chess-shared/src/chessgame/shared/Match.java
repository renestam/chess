/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author arvid.renestam
 */
public final class Match implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Player whitePlayer, blackPlayer;
    private boolean currentPlayerIsWhite;
    private String winner = ""; // white, black, stalemate
    
    private final ArrayList<Move> moves = new ArrayList();
    private Integer movesPlayed;
    
    public Match(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public Match(Player whitePlayer, Player blackPlayer, boolean currentPlayerIsWhite) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPlayerIsWhite = currentPlayerIsWhite;
    }
    
    public Match(Player whitePlayer, Player blackPlayer, String winner, int movesPlayed) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.winner = winner;
        this.movesPlayed = movesPlayed;
    }
    
    public Player getWhitePlayer() { return whitePlayer; }
    public Player getBlackPlayer() { return blackPlayer; }
    public String getWinner() { return winner; }
    public ArrayList<Move> getMoves() { return moves; }
    public boolean getCurrentPlayerIsWhite() { return currentPlayerIsWhite; }
    public boolean isActive() { return winner == null || winner.isBlank(); }
    
    public int getMovesPlayed() { 
        if (movesPlayed != null) return movesPlayed;
        return moves.size(); 
    }
    
    public Move getLastMove() {
        if (moves == null || moves.isEmpty()) return null;
        return moves.get(moves.size() - 1);
    }
    
    public Move getSecondLastMove() {
        if (moves == null || moves.size() < 2) return null;
        return moves.get(moves.size() - 2);
    }
    
    public void addMove(Move move) { moves.add(move); }
    
    public Match setCurrentPlayerIsWhite(boolean currentPlayerIsWhite) {
        this.currentPlayerIsWhite = currentPlayerIsWhite;
        return this;
    }
    
    public void setWinner(String newWinner) {
        if (!winner.isBlank()) return;
        winner = newWinner;
        
        // increment players' scores
        switch (winner) {
            case "white" -> whitePlayer.incrementScore();
            case "black" -> blackPlayer.incrementScore();
            case "stalemate" -> {
                whitePlayer.incrementScoreStalemate();
                blackPlayer.incrementScoreStalemate();
            }
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

        if (winner.equals("white")) {
            return isWhitePlayer ? " (W)" : " (L)";
        } else { // winner is "black"
            return isWhitePlayer ? " (L)" : " (W)";
        }
    }
    
}
