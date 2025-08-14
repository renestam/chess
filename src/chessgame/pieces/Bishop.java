package chessgame.pieces;

import chessgame.Move;
import chessgame.Piece;
import chessgame.Square;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arvid.renestam
 */
public final class Bishop extends Piece {
    
    public Bishop(boolean isWhite) {
        super("bishop", isWhite);
    }
    
    private final int[] moveDirections = {-9, -7, 7, 9};
    
    @Override
    public void calculatePossibleMoves(
        Square[] squares, 
        Square currentSquare, 
        Move lastMove
    ) {
        super.calculatePossibleMoves(squares, currentSquare, lastMove);
        
        addPossibleMovesForSquareFromOffsets(
            squares, 
            currentSquare, 
            moveDirections,
            true
        );
    }
    
}
