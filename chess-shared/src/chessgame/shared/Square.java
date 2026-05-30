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
public final class Square implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int index;
    private final int column;
    private final int row;
    private final boolean isWhite;
    private Piece piece;

    public Square(int index, Piece piece, boolean isWhite) {
        this.index = Board.indexIsValid(index) ? index : 0;
        this.piece = piece;
        this.isWhite = isWhite;
        this.column = this.index % 8;
        this.row = this.index / 8;
    }
    
    public int getIndex() { return index; }
    public int getColumn() { return column; }
    public int getRow() { return row; }
    public boolean isWhite() { return isWhite; }
    
    public Piece getPiece() { return piece; }
    public void setPiece(Piece piece) { this.piece = piece; }
    
    public boolean hasPiece() {
        return piece != null && piece.getName() != null && !piece.getName().isBlank();
    }
    
    public void removePiece() {
        this.piece = null;
    }
    
    public boolean hasSameColoredPiece(Square other) {
        return this.hasPiece() && other.hasPiece() && 
               this.piece.isWhite() == other.getPiece().isWhite();
    }
    
    public boolean hasOppositeColoredPiece(Square other) {
        return this.hasPiece() && other.hasPiece() && 
               this.piece.isWhite() != other.getPiece().isWhite();
    }
    
    public boolean isRelativeRow(int rank, boolean checkWhite) {
        int relativeRow = checkWhite ? 7 - rank : rank;
        return this.row == relativeRow;
    }
    
}
