/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

/**
 *
 * @author arvid.renestam
 */
public final class Move {
    private final Square oldSquare;
    private final Square newSquare;
    
    private Piece oldSquarePiece;
    private Piece newSquarePiece;
    
    private Square[] additionalSquares = null;
    private Piece capturedPiece;
    
    public boolean isCapture = false;
    public boolean isPromotion = false;
    public boolean isEnPassant = false;
    public boolean isCastle = false;
    
    public boolean isValid = true;
    
    public Move() {
        this.oldSquare = new Square();
        this.newSquare = new Square();
    }

    public Move(Square oldSquare, Square newSquare) {
        this.oldSquare = oldSquare;
        this.newSquare = newSquare;
        this.oldSquarePiece = oldSquare.piece;
        this.newSquarePiece = newSquare.piece;
    }
    
    public Square getOldSquare() {
        return oldSquare;
    }
    
    public Square getNewSquare() {
        return newSquare;
    }
    
    public Piece getOldSquarePiece() {
        return oldSquarePiece;
    }
    
    public Piece getNewSquarePiece() {
        return newSquarePiece;
    }
    
    public Piece getCapturedPiece() {
        return capturedPiece;
    }
    
    public Square[] getAdditionalSquares() {
        return additionalSquares;
    }
    
    public void setAdditionalSquare(Square[] square) {
        additionalSquares = square;
    }
    
    public void setCapturedPiece(Piece piece) {
        capturedPiece = piece;
    }
    
    public void setIsLastMove(boolean isLastMove) {
        oldSquare.isLastMove = isLastMove;
        newSquare.isLastMove = isLastMove;
        
        if (isLastMove) {
            oldSquare.isSelected = false;
            newSquare.isSelected = false;
        }
    }
    
}
