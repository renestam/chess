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
public final class Knight extends Piece{
    public Knight(boolean isWhite) {
        super("knight", isWhite);
    }
    
    private final int[] indexOffsets = {-17, -15, -10, -6, 6, 10, 15, 17};
    
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
            indexOffsets,
            false
        );
    }
}
