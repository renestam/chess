/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared;

import java.io.Serializable;

/**
 *
 * @author arvid.renestam
 */
public final class Move implements Serializable {
    
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
        this.oldSquare = new Square(64, null, true);
        this.newSquare = new Square(64, null, true);
    }

    public Move(Square oldSquare, Square newSquare) {
        this.oldSquare = oldSquare;
        this.newSquare = newSquare;
        this.oldSquarePiece = oldSquare.getPiece();
        this.newSquarePiece = newSquare.getPiece();
    }
    
    public Square getOldSquare() { return oldSquare; }
    public Square getNewSquare() { return newSquare; }
    public Piece getOldSquarePiece() { return oldSquarePiece; }
    public Piece getNewSquarePiece() { return newSquarePiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public Square[] getAdditionalSquares() { return additionalSquares; }
    
    public void setAdditionalSquare(Square[] square) { additionalSquares = square; }
    public void setCapturedPiece(Piece piece) { capturedPiece = piece; }
    
}
