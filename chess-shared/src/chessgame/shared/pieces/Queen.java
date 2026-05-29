package chessgame.shared.pieces;

import chessgame.shared.Move;
import chessgame.shared.Piece;
import chessgame.shared.Square;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arvid.renestam
 */
public final class Queen extends Piece{
    
    public Queen(boolean isWhite) {
        super("queen", isWhite);
    }
    
    private final int[] indexOffsets = {-9, -8, -7, -1, 1, 7, 8, 9};

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
            true
        );
    }
    
}
