package chessgame.pieces;

import chessgame.Board;
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
public final class Pawn extends Piece {
    
    public Pawn(boolean isWhite) {
        super("pawn", isWhite);
    }
    
    // positions relative to the current position
    private final int[] moveOffsets = {-16, -8};
    private final int[] captureOffsets = {-9, -7};
    private final int[] enPassantOffsets = {-1, 1};
    
    @Override
    public void calculatePossibleMoves(
        Square[] squares, 
        Square currentSquare, 
        Move lastMove
    ) {
        super.calculatePossibleMoves(squares, currentSquare, lastMove);

        int offsetDirection = isWhite() ? 1 : -1;
        int currentIndex = currentSquare.getIndex();
        
        // one and two steps forward
        for (int index : moveOffsets) {
            // don't allow two steps if not on first rank
            if (!currentSquare.isRelativeRow(1) && index == -16) {
                continue;
            }
            
            int newIndex = currentIndex + index * offsetDirection;
            
            // move only possible if square doesn't have piece
            if (!squares[newIndex].hasPiece()) {
                Move move = new Move(currentSquare, squares[newIndex]);
                handlePromotion(move, squares, currentSquare, newIndex);
                addPossibleMove(move);
            }
        }
        
        // captures
        for (int i = 0; i < captureOffsets.length; i++) {
            int newIndex = currentIndex + captureOffsets[i] * offsetDirection;
            
            if (!Board.indexIsValid(newIndex)) continue;
            if (!newIndexIsValid(squares, currentIndex, newIndex)) continue;
            
            // capture is possible if new square has different colored piece
            if (currentSquare.hasOppositeColoredPiece(squares[newIndex])) {
                Move move = new Move(currentSquare, squares[newIndex]);
                move.isCapture = true;
                handlePromotion(move, squares, currentSquare, newIndex);
                addPossibleMove(move);
                
                if (squares[newIndex].piece.getName().equals("king")) {
                    squares[newIndex].piece.isAttacked = true;
                }
            }
            
            // en passant en passant captures, look for pieces to the right and left
            for (int j = 0; j < enPassantOffsets.length; j++) {
                // index of the piece getting captured
                int captureIndex = currentIndex + enPassantOffsets[i] * offsetDirection; 

                // criterias for en passant (logiken är lite krånglig, jag vet)
                boolean captureIndexIsLastMove = lastMove.getNewSquare().getIndex() == captureIndex;
                boolean lastMoveStartedOnSecondRank = lastMove.getOldSquare().isRelativeRow(1, lastMove.getNewSquare().piece.isWhite());
                boolean lastMoveIsOnFourthRank = lastMove.getNewSquare().isRelativeRow(3, lastMove.getNewSquare().piece.isWhite());
                if (captureIndexIsLastMove && lastMoveStartedOnSecondRank && lastMoveIsOnFourthRank) {
                    Move move = new Move(currentSquare, squares[newIndex]);
                    move.isEnPassant = true;
                    move.setCapturedPiece(squares[captureIndex].piece);
                    Square[] additionalSquares = {squares[captureIndex]};
                    move.setAdditionalSquare(additionalSquares);
                    addPossibleMove(move);
                }
            }
        }
    }
    
    private void handlePromotion(
        Move move, 
        Square[] squares, 
        Square currentSquare, 
        int newIndex
    ) {
        if (squares[newIndex].isRelativeRow(7, currentSquare.piece.isWhite())) {
            move.isPromotion = true;
        }
    }
    
}
