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
    
    private static final long serialVersionUID = 1L;
    
    private final Square oldSquare; // where the piece started from
    private final Square newSquare; // where the piece ended up
    
    private final Piece oldSquarePiece; // initial piece of old square
    private final Piece newSquarePiece; // initial piece of new square
    
    private Square[] additionalSquares = null;
    private Piece capturedPiece;
    
    private boolean isCapture = false;
    private boolean isPromotion = false;
    private boolean isEnpassant = false;
    private boolean isCastle = false;
    
    private boolean isValid = true;
    public boolean isSimulation = false;

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
    
    public boolean isCapture() { return isCapture; }
    public boolean isPromotion() { return isPromotion; }
    public boolean isEnpassant() { return isEnpassant; }
    public boolean isCastle() { return isCastle; }
    public boolean isValid() { return isValid; }
    public boolean isSimulation() { return isSimulation; }
    
    public void setCapture(boolean b) { isCapture = b; }
    public void setPromotion(boolean b) { isPromotion = b; }
    public void setEnpassant(boolean b) { isEnpassant = b; }
    public void setCastle(boolean b) { isCastle = b; }
    public void setValid(boolean b) { isValid = b; }
    public void setSimulation(boolean b) { isSimulation = b; }
    
    // Because the server and client use different board instances, 
    // the client can't use the squares passed by server in Move object.
    // That's basically why we need this method. And honestly,
    // I pretty much felt like a genius when I figured this out.
    public Move bindToLocalBoard(Board localBoard) {
        Square localOld = localBoard.getSquares()[this.oldSquare.getIndex()];
        Square localNew = localBoard.getSquares()[this.newSquare.getIndex()];

        // Create a clean local Move copy
        Move localizedMove = new Move(localOld, localNew);
        localizedMove.isCapture = this.isCapture;
        localizedMove.isPromotion = this.isPromotion;
        localizedMove.isEnpassant = this.isEnpassant;
        localizedMove.isCastle = this.isCastle;
        localizedMove.isValid = this.isValid;

        if (this.additionalSquares != null) {
            Square[] localAdditional = new Square[this.additionalSquares.length];
            for (int i = 0; i < this.additionalSquares.length; i++) {
                localAdditional[i] = localBoard.getSquares()[this.additionalSquares[i].getIndex()];
            }
            localizedMove.setAdditionalSquare(localAdditional);
        }
        localizedMove.setCapturedPiece(this.capturedPiece);

        return localizedMove;
    }
    
}
