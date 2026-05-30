/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared.pieces;

import chessgame.shared.Move;
import chessgame.shared.Piece;
import chessgame.shared.Square;

/**
 *
 * @author arvid.renestam
 */
public final class Rook extends Piece{
    
    public Rook(boolean isWhite) {
        super("rook", isWhite);
    }
    
    private final int[] indexOffsets = {-8, -1, 1, 8};
    
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
