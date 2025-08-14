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
public final class King extends Piece {
    
    public King(boolean isWhite) {
        super("king", isWhite);
    }
    
    private final int[] indexOfffsets = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final int[] rookOffsets = {-4, 3};
    private final int[] kingCastleOffsets = {-2, 2};
    private final int[] rookCastleOffsets = {-1, 1};
    
    @Override
    public void calculatePossibleMoves(
        Square[] squares, 
        Square currentSquare, 
        Move lastMove
    ) {
        super.calculatePossibleMoves(squares, currentSquare, lastMove);
        
        // normal moves
        addPossibleMovesForSquareFromOffsets(
            squares, 
            currentSquare, 
            indexOfffsets, 
            false
        );
        
        // castling
        if (hasMoved) return; // no castling if king has moved
        
        for (int i = 0; i < 2; i++) {
            int currentIndex = currentSquare.getIndex();
            Square rookSquare = squares[currentIndex + rookOffsets[i]];

            // make sure rook hasn't moved
            if (rookSquare.piece.hasMoved) continue;

            boolean isRook = rookSquare.piece.getName().equals("rook");
            boolean isSameColor = rookSquare.hasSameColoredPiece(currentSquare);

            if (isRook && isSameColor) {
                Square newKingSquare = squares[currentIndex + kingCastleOffsets[i]];
                Square newRookSquare = squares[currentIndex + rookCastleOffsets[i]];

                // make sure no piece is blocking castle
                if (newKingSquare.hasPiece() || newRookSquare.hasPiece()) {
                    continue;
                }
                else if (i == 0) {
                    // check if the b rank square is empty
                    if (squares[currentIndex - 3].hasPiece()) {
                        continue;
                    }
                }

                Move move = new Move(currentSquare, newKingSquare);

                move.isCastle = true;
                Square[] additionalMoveSquares = {rookSquare, newRookSquare};
                move.setAdditionalSquare(additionalMoveSquares);

                addPossibleMove(move);

 
            }
        }
        
    }
    
}
