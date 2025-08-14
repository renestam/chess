package chessgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arvid.renestam
 */
public final class Square {
    private final int index; // board index (0-63, top left)
    private final int column; // board column (0-7, left to right)
    private final int row; // board row (0-7, top to bottom)
    private final boolean isWhite;
    
    public Piece piece;
    
    public boolean isSelected;
    public boolean isLastMove;
    public boolean isPossibleMove;
    
    public Square() {
        this.index = 64;
        this.piece = new Piece();
        this.isWhite = true;
        this.isSelected = false;
        this.isPossibleMove = false;
        this.column = -1;
        this.row = -1;
    }

    public Square(
        int index,
        Piece piece,
        boolean isWhite, 
        boolean isSelected, 
        boolean isLastMove,
        boolean isPossibleMove
    ) {
        if (Board.indexIsValid(index)) {
            this.index = index;
        } else {
            this.index = 0;
        }
        this.piece = piece;
        this.isWhite = isWhite;
        this.isSelected = isSelected;
        this.isLastMove = isLastMove;
        this.isPossibleMove = isPossibleMove;
        this.column = index % 8;
        this.row = index / 8;
    }
    
    // colors
    static Color whiteColor = new Color(227, 199, 167);
    static Color blackColor = new Color(58, 42, 26);
    static Color selectedWhiteColor = new Color(217, 155, 84);
    static Color selectedBlackColor = new Color(131, 80, 29);
    static Color lastMoveWhiteColor = new Color(178, 227, 167);
    static Color lastMoveBlackColor = new Color(80, 110, 50);
    static Color possibleMoveColor = new Color(244, 109, 0);
    
    // getters
    public int getIndex() {
        return index;
    }
    
    public int getColumn() {
        return column;
    }
    
    public int getRow() {
        return row;
    }
    
    public boolean isWhite() {
        return isWhite;
    };
    
    // piece methods
    public boolean hasPiece() {
        return piece != null && !piece.getName().isBlank();
    }
    
    public void removePiece() {
        piece = new Piece();
    }
    
    public boolean hasSameColoredPiece(Square square) {
        return this.hasPiece() && square.hasPiece() && 
                this.piece.isWhite() == square.piece.isWhite();
    }
    
    public boolean hasOppositeColoredPiece(Square square) {
        return this.hasPiece() && square.hasPiece() && 
                this.piece.isWhite() != square.piece.isWhite();
    }
    
    // 0: row closest to the player whose move it is, 7: farthest away    
    public boolean isRelativeRow(int rank, boolean isWhite) {
        int relativeRow = isWhite ? 7 - rank : rank;
        return this.row == relativeRow;
    }
    public boolean isRelativeRow(int rank) {
        int relativeRow = this.piece.isWhite() ? 7 - rank : rank;
        return this.row == relativeRow;
    }
    
    // draw the square, piece and possible move circle
    public void draw(Graphics g, ImageObserver observer, int squareSize) {
        // set square color
        Color squareColor;
        if (isSelected) {
            squareColor = isWhite ? selectedWhiteColor : selectedBlackColor;
        } else if (isLastMove) {
            squareColor = isWhite ? lastMoveWhiteColor : lastMoveBlackColor;
        } else {
            squareColor = isWhite ? whiteColor : blackColor;
        }
        g.setColor(squareColor);
        
        // calculate position based on index of square
        int posX = index % 8 * squareSize + Panel.PADDING;
        int posY = index / 8 * squareSize + (Panel.height - squareSize * 8) / 2;
        
        // square
        g.fillRect( 
            posX,
            posY,
            squareSize,
            squareSize
        );
        
        // piece
        piece.draw(g, observer, posX, posY, squareSize);
        
        // possible move circle
        if (isPossibleMove) {
            int size = squareSize / 5;
            int padding = (squareSize - size) / 2;
            
            g.setColor(possibleMoveColor);
            g.fillOval(posX + padding, posY + padding, size, size);
        }
    }
}
