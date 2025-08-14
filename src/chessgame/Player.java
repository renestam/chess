/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

/**
 *
 * @author arvid.renestam
 */
public final class Player {
    private final String name;
    private float score;

    public Player(String name, float score) {
        this.name = name;
        this.score = score;
    }
    
    public void incrementScore() {
        score++;
    }
    
    public void incrementScoreStalemate() {
        score += 0.5;
    }
    
    public float getScore() {
        return score;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        String scoreString = String.valueOf(score);
        if (scoreString.endsWith(".0")) {
            scoreString = scoreString.substring(0, scoreString.length() -2);
        }
        return name + " (" + scoreString  + ")";
    }
}
