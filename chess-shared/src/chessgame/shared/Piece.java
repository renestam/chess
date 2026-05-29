/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared;

import java.util.ArrayList;

/**
 *
 * @author arvid.renestam
 */
public class Piece {
    private final String name;
    private final boolean isWhite;
    
    public boolean hasMoved = false;
    public boolean hadMovedLastMove = false;
    public boolean isAttacked = false;
    
    private final ArrayList<Move> possibleMoves = new ArrayList<>();
    
    public Piece() {
        this.name = "";
        this.isWhite = true;
    }

    public Piece(String pieceName, boolean isWhite) {
        this.name = pieceName;
        this.isWhite = isWhite;
    }
    
    // getters
    public String getName() { return name; }
    public boolean isWhite() { return isWhite; }
    public ArrayList<Move> getPossibleMoves() { return possibleMoves; }
    
    public Move getPossibleMove(int index) {
        for (Move move : possibleMoves) {
            if (move.getNewSquare().getIndex() == index) {
                return move;
            }
        }
        return null;
    }
    
    // setters
    public void addPossibleMove(Move move) {
        if (Board.indexIsValid(move.getOldSquare().getIndex())) {
            possibleMoves.add(move);
        }
    }
    
    // decide which squares a piece can move to - overridden in derived classes
    public void calculatePossibleMoves(
        Square[] squares, 
        Square currentSquare, 
        Move lastMove
    ) {
        possibleMoves.clear();
    }
    
    // checks if the new index doesn't wrap around the board
    protected boolean newIndexIsValid(
        Square[] squares, 
        int startingIndex, 
        int newIndex
    ) {
        int startingColumn = squares[startingIndex].getColumn();
        int newColumn = squares[newIndex].getColumn();
        
        // no piece has offset bigger than 2
        return Math.abs(startingColumn - newColumn) < 3;
    }
    
    protected void addPossibleMovesForSquareFromOffsets(
        Square[] squares,
        Square currentSquare, 
        int[] indexOffsets,
        boolean isSlidingPiece
    ) {
        int currentIndex = currentSquare.getIndex();
        
        for (int direction : indexOffsets) {
            int lastAddedIndex = currentIndex;

            int loopCount = isSlidingPiece ? 7 : 1;
            for (int j = 1; j < loopCount + 1; j++) {                                
                int newIndex = currentIndex + direction * j;
                if (!Board.indexIsValid(newIndex)) break;
                if (!newIndexIsValid(squares, lastAddedIndex, newIndex)) {
                    break;
                }

                // check if same colored piece is blocking 
                if (squares[newIndex].hasSameColoredPiece(currentSquare)) {
                    break;
                }

                // everything's good - add move
                Move move = new Move(currentSquare, squares[newIndex]);
                
                addPossibleMove(move);
                lastAddedIndex = newIndex;
                
                // check if opposite colored piece is blocking (add move but don't continue further)
                if (squares[newIndex].hasOppositeColoredPiece(currentSquare)) {
                    move.isCapture = true;
                    
                    // FIXED: Replaced direct field access (.piece.name) with encapsulated getters (.getPiece().getName())
                    Piece targetPiece = squares[newIndex].getPiece();
                    if (targetPiece != null && "king".equals(targetPiece.getName())) {
                        targetPiece.isAttacked = true;
                    }
                    
                    break;
                }
            }
        }
    }
    
}
