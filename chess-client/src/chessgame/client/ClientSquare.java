/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Square;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 *
 * @author arvid
 */
public class ClientSquare {
    
    private final Square square;
    
    private final boolean isSelected;
    private final boolean isLastMove;
    private final boolean isPossibleMove;
    
    public ClientSquare(Square square, boolean isSelected, boolean isPossibleMove, boolean isLastMove) {
        this.square = square;
        this.isSelected = isSelected;
        this.isPossibleMove = isPossibleMove;
        this.isLastMove = isLastMove;
    }
    
    // colors
    private static final Color whiteColor = new Color(227, 199, 167);
    private static final Color blackColor = new Color(58, 42, 26);
    private static final Color selectedWhiteColor = new Color(217, 155, 84);
    private static final Color selectedBlackColor = new Color(131, 80, 29);
    private static final Color lastMoveWhiteColor = new Color(178, 227, 167);
    private static final Color lastMoveBlackColor = new Color(80, 110, 50);
    private static final Color possibleMoveColor = new Color(244, 109, 0);
    
    // draw the square, piece and possible move circle
    public void draw(Graphics g, ImageObserver observer, int squareSize, Dimension panelSize) {
        boolean isWhite = square.isWhite();
        int index = square.getIndex();
        
        // set square color
        Color squareColor;
        if (this.isSelected) {
            squareColor = isWhite ? selectedWhiteColor : selectedBlackColor;
        } else if (this.isLastMove) {
            squareColor = isWhite ? lastMoveWhiteColor : lastMoveBlackColor;
        } else {
            squareColor = isWhite ? whiteColor : blackColor;
        }
        g.setColor(squareColor);
        
        // calculate position based on index of square
        int posX = index % 8 * squareSize + GamePanel.PADDING; 
        int posY = index / 8 * squareSize + (panelSize.height - squareSize * 8) / 2;
        
        // square
        g.fillRect( 
            posX,
            posY,
            squareSize,
            squareSize
        );
        
        // piece
        if (square.hasPiece()) {
            ClientPiece piece = new ClientPiece(square.getPiece());
            piece.draw(g, observer, posX, posY, squareSize);
        }
        
        // possible move circle
        if (this.isPossibleMove) {
            int size = squareSize / 5;
            int padding = (squareSize - size) / 2;
            
            g.setColor(possibleMoveColor);
            g.fillOval(posX + padding, posY + padding, size, size);
        }
    }
    
    public Square getSharedSquare() {
        return square;
    }
}