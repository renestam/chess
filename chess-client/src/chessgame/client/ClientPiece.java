/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Piece;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;

/**
 *
 * @author arvid
 */
public class ClientPiece {
    
    private final Piece piece;
    
    public ClientPiece(Piece piece) {
        this.piece = piece;
    }
    
    private static final String IMAGE_PATH = "/assets/images/";
    
    void draw(
        Graphics g, 
        ImageObserver observer,
        int squarePosX, 
        int squarePosY, 
        int squareSize
    ) {
        boolean isWhite = piece.isWhite();
        String name = piece.getName();
        
        if (name.isEmpty()) return;
        
        // make pawn padding a bit bigger
        int padding = name.equals("pawn") ? squareSize / 13 * 3 : squareSize / 5;
        int size = squareSize - 2 * padding;
        
        // draw image
        String path = "%s/%s_%s.png".formatted(IMAGE_PATH, isWhite ? "w" : "b", name);
        try {
            Image image = Toolkit.getDefaultToolkit().getImage(
                getClass().getResource(path)
            );
            g.drawImage(
                image, 
                squarePosX + padding, 
                squarePosY + padding,
                size,
                size,
                observer
            );
        } catch (Exception e) {
            // draw square instead
            g.setColor(isWhite ? Color.white : Color.black);
            g.fillRect(squarePosX + padding, squarePosY + padding, size, size);
        }
    }
    
}
