/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.shared;

import chessgame.shared.pieces.Pawn;
import chessgame.shared.pieces.Queen;
import chessgame.shared.pieces.Rook;
import chessgame.shared.pieces.King;
import chessgame.shared.pieces.Bishop;
import chessgame.shared.pieces.Knight;

/**
 *
 * @author arvid.renestam
 */
public final class Board {
    private final Square[] squares = new Square[64];
    
    private King whiteKing;
    private King blackKing;
    
    private Match currentMatch;
    private boolean currentMatchHasStarted = false;
    private boolean whiteToMove;
    
    private Move lastMove;
    private Move secondLastMove;

    public Board() {
        whiteToMove = true;
        lastMove = new Move();
        secondLastMove = new Move();
        setStartingPosition();        
    }
    
    // getters
    public Square[] getSquares() { return squares; }
    public boolean whiteToMove() { return whiteToMove; }
    public Move getLastMove() { return lastMove; }
    public Match getCurrentMatch() { return currentMatch; }
    public boolean currentMatchHasStarted() { return currentMatchHasStarted; }
    public King getWhiteKing() { return whiteKing; }
    public King getBlackKing() { return blackKing; }
    
    // needs to get called before a new match starts
    public void startNewMatch(Match match) {
        whiteToMove = true;
        currentMatchHasStarted = true;
        currentMatch = match;
        lastMove = new Move();
        
        setStartingPosition();
        calculatePossibleMoves();
    }
    
    public boolean checkMatchIsWon() {
        // look for any valid possible moves for the side to move
        boolean canMove = false;
        for (Square square : squares) {
            // FIXED: Used .getPiece() safely
            if (square.hasPiece() && square.getPiece().isWhite() == whiteToMove) {
                boolean moveIsValid = false;
                for (Move move : square.getPiece().getPossibleMoves()) {
                    if (move.isValid) {
                        moveIsValid = true;
                        break;
                    }
                }
                if (moveIsValid) {
                    canMove = true;
                    break;
                }
            }
        }
        
        boolean whiteIsAttacked = whiteKing.isAttacked;
        boolean blackIsAttacked = blackKing.isAttacked;
        
        if (whiteToMove && !canMove && whiteIsAttacked) {
            currentMatch.setWinner("black");
        } else if (!whiteToMove && !canMove && blackIsAttacked) {
            currentMatch.setWinner("white");
        } else if (!canMove) {
            currentMatch.setWinner("stalemate");
        }
        
        // no one has won, continue game
        else {
            return false;
        }
        
        // match is won
        currentMatchHasStarted = false;
        return true;
    }
    
    private void setStartingPosition() {
        for (int i = 0; i < squares.length; i++) {
            // every other square is black
            boolean isWhite = i % 2 == 0 && (i / 8) % 2 == 0 
                    || i % 2 != 0 && (i / 8) % 2 != 0;
            
            // FIXED: Removed UI states from the Square constructor
            squares[i] = new Square(
                i, 
                startingPositionPiece(i), 
                isWhite
            );
        }
    }
    
    // returns the piece that should be on the index in starting position
    private Piece startingPositionPiece(int i) {
        if ((i > 7 && i < 16) || (i > 47 && i < 56)) {
            return new Pawn(i > 47 && i < 56);
        }
        if (i == 1 || i == 6 || i == 57 || i == 62) {
            return new Knight(i == 57 || i == 62);
        } 
        if (i == 2 || i == 5 || i == 58 || i == 61) {
            return new Bishop(i == 58 || i == 61);
        } 
        if (i == 0 || i == 7 || i == 56 || i == 63) {
            return new Rook(i == 56  || i == 63);
        }
        if (i == 3 || i == 59) {
            return new Queen(i == 59);
        } 
        if (i == 4 || i == 60) {
            King king = new King(i == 60);
            if (i == 60) {
                whiteKing = king;
            } else {
                blackKing = king;
            }
            return king;
        } 
        // FIXED: Return null instead of an empty Piece object for empty squares
        return null;
    }
    
    public boolean movePiece(Move move) {
        if (move == null) return false;
        if (!move.isValid) return false;
        if (!currentMatchHasStarted) return false;
        movePiece(move, false);
        calculatePossibleMoves();
        whiteToMove = !whiteToMove;
        return true; // successfully moved piece;
    }
    
    // only called directly when calculating possible moves (lacks validation)
    private void movePiece(Move move, boolean isRevert) {
        Square oldSquare = move.getOldSquare();
        Square newSquare = move.getNewSquare();
        
        Piece oldSquarePiece = move.getOldSquarePiece();
        Piece newSquarePiece = move.getNewSquarePiece();
        
        if (oldSquarePiece == null) return; // nothing to move
        
        boolean isWhite = oldSquarePiece.isWhite();
        
        if (isWhite) currentMatch.incrementMovesPlayed();
        if (!currentMatchHasStarted) currentMatchHasStarted = true;
        
        // FIXED: Removed the .setIsLastMove() calls entirely. We just track the Move objects now.
        if (!isRevert) {
            secondLastMove = lastMove;
            lastMove = new Move(oldSquare, newSquare);          
        } else {
            lastMove = secondLastMove;
        }

        // update pieces to reflect the move
        if (!isRevert) {
            newSquare.setPiece(oldSquarePiece);
            oldSquare.removePiece(); 
        } else {
            oldSquare.setPiece(oldSquarePiece);
            newSquare.setPiece(newSquarePiece);
        }

        // promotion
        if (move.isPromotion) {
            if (!isRevert) {
                newSquare.setPiece(new Queen(isWhite));
            } else {
                newSquare.setPiece(move.getNewSquarePiece());
                oldSquare.setPiece(move.getOldSquarePiece());
            }
        }

        // en passant
        if (move.isEnPassant) {
            Square capturedPawnSquare = move.getAdditionalSquares()[0];
            if (!isRevert) {
                capturedPawnSquare.removePiece();
            } else {
                capturedPawnSquare.setPiece(move.getCapturedPiece());
            }
        }

        // castling
        if (move.isCastle) {
            Square oldRookSquare = move.getAdditionalSquares()[0];
            Square newRookSquare = move.getAdditionalSquares()[1];
            
            if (!isRevert) {
                newRookSquare.setPiece(oldRookSquare.getPiece());
                oldRookSquare.removePiece();
            } else {
                oldRookSquare.setPiece(newRookSquare.getPiece());
                newRookSquare.removePiece();
            }
        }
        
        // revert the previously made move
        if (!isRevert) {
            if (newSquare.getPiece().hasMoved) {
                newSquare.getPiece().hadMovedLastMove = true;
            } else {
                newSquare.getPiece().hasMoved = true;
            }
        } else {
            oldSquare.getPiece().hasMoved = oldSquare.getPiece().hadMovedLastMove;
            if (newSquare.hasPiece()) {
                newSquare.getPiece().hasMoved = newSquare.getPiece().hadMovedLastMove;
            }
        }
    }


    // goes through all pieces and updates possibleMoves
    public void calculatePossibleMoves() {
        for (Square square : squares) {
            if (square.hasPiece() && square.getPiece().isWhite() == whiteToMove) {
                square.getPiece().calculatePossibleMoves(
                    squares, 
                    square, 
                    lastMove
                );
                
                // go through the piece's possible moves for validation
                for (Move move : square.getPiece().getPossibleMoves()) {
                    // silently move piece by later reverting the move
                    movePiece(move, false);
                    
                    // calculate opponents' moves after the move
                    for (Square subsquare : squares) {
                        if (subsquare.hasPiece() && subsquare.getPiece().isWhite() == !whiteToMove) {
                            subsquare.getPiece().calculatePossibleMoves(
                                squares, 
                                subsquare, 
                                move
                            );
                            
                            boolean moveIsValid = true;
                            
                            // go through the moves to see if any attack the king, which would make the initial move invalid
                            for (Move submove : subsquare.getPiece().getPossibleMoves()) {
                                Square newSquare = submove.getNewSquare();
                                // FIXED: Safe piece checking
                                if (newSquare.hasPiece() && 
                                    "king".equals(newSquare.getPiece().getName()) && 
                                    newSquare.getPiece().isWhite() == whiteToMove) {
                                    moveIsValid = false;
                                    break;
                                }
                            }
                            
                            if (!moveIsValid) {
                                move.isValid = false;
                                break;
                            }
                        }
                    }
                    
                    // revert the move
                    movePiece(move, true);
                }
            }
        }
        
        // re-check if the king is under attack
        King kingToCheck = whiteToMove ? whiteKing : blackKing;
        kingToCheck.isAttacked = false;
        for (Square square : squares) {
            if (square.hasPiece() && square.getPiece().isWhite() != whiteToMove) {
                for (Move move : square.getPiece().getPossibleMoves()) {
                    if (move.isValid && move.getNewSquare().getPiece() == kingToCheck) {
                        kingToCheck.isAttacked = true;
                        break;
                    }
                }
            }
        }
    }
    
    // checks if the index exists on the board (0-63 is valid)
    public static boolean indexIsValid(int index) {
        return index >= 0 && index < 64;
    }
    
}