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
import java.util.ArrayList;

/**
 *
 * @author arvid.renestam
 */
public final class Board {
    
    private final Square[] squares = new Square[64];
    
    private King whiteKing;
    private King blackKing;
    
    private Match currentMatch;
    private ArrayList<Move> moves;
    
    private boolean currentMatchHasStarted = false;
    private boolean whiteToMove = true;

    public Board() {
        setStartingPosition();        
    }
    
    // getters
    public Square[] getSquares() { return squares; }
    public King getWhiteKing() { return whiteKing; }
    public King getBlackKing() { return blackKing; }
    public Match getCurrentMatch() { return currentMatch; }
    public boolean currentMatchHasStarted() { return currentMatchHasStarted; }
    public boolean whiteToMove() { return whiteToMove; }
    
    // needs to get called before a new match starts
    public void startNewMatch(Match match, boolean currentPlayerIsWhite) {
        whiteToMove = true;
        currentMatchHasStarted = true;
        currentMatch = match;
        moves = currentMatch.getMoves();
        
        setStartingPosition();
        calculateValidMoves(currentPlayerIsWhite);
    }
    
    public boolean checkMatchIsWon() {
        // look for any valid possible moves for the side to move
        boolean canMove = false;
        for (Square square : squares) {
            if (square.hasPiece() && square.getPiece().isWhite() == whiteToMove) {
                boolean moveIsValid = false;
                for (Move move : square.getPiece().getPossibleMoves()) {
                    if (move.isValid()) {
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
        else { return false; }
        
        // match is won
        currentMatchHasStarted = false;
        return true;
    }
    
    public boolean makeMove(Move move, boolean currentPlayerIsWhite) {
        if (move == null) return false;
        if (!move.isValid()) return false;
        if (!currentMatchHasStarted) return false;
        if (!movePiece(move, false)) return false;
        calculateValidMoves(currentPlayerIsWhite);
        whiteToMove = !whiteToMove;
        return true; // successfully moved piece;
    }
    
    // only called directly when calculating possible moves (lacks validation)
    private boolean movePiece(Move move, boolean isRevert) {
        Square oldSquare = move.getOldSquare();
        Square newSquare = move.getNewSquare();
        
        Piece oldSquarePiece = move.getOldSquarePiece();
        Piece newSquarePiece = move.getNewSquarePiece();
        
        if (oldSquarePiece == null) return false; // nothing to move

        if (!move.isSimulation) {
            if (!currentMatchHasStarted) currentMatchHasStarted = true;
            
            if (isRevert) {
                // Only pop from ledger trace if it's a permanent real game action
                if (!move.isSimulation && !moves.isEmpty()) {
                    moves.removeLast();
                }
            } else {
                Move newMove = new Move(oldSquare, newSquare);
                moves.add(newMove);
            }
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
        if (move.isPromotion()) {
            if (!isRevert) {
                newSquare.setPiece(new Queen(oldSquarePiece.isWhite()));
            } else {
                newSquare.setPiece(move.getNewSquarePiece());
                oldSquare.setPiece(move.getOldSquarePiece());
            }
        }

        // en passant
        else if (move.isEnpassant()) {
            Square capturedPawnSquare = move.getAdditionalSquares()[0];
            if (!isRevert) {
                capturedPawnSquare.removePiece();
            } else {
                capturedPawnSquare.setPiece(move.getCapturedPiece());
            }
        }

        // castling
        else if (move.isCastle()) {
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
        
        if (!move.isSimulation) {
            if (isRevert) {
                oldSquare.getPiece().hasMoved = oldSquare.getPiece().hadMovedLastMove;
                if (newSquare.hasPiece()) {
                    newSquare.getPiece().hasMoved = newSquare.getPiece().hadMovedLastMove;
                }
            } else {
                if (newSquare.getPiece().hasMoved) {
                    newSquare.getPiece().hadMovedLastMove = true;
                } else {
                    newSquare.getPiece().hasMoved = true;
                }
            }
        }
        
        return true; // successfully made move
    }


    // goes through all pieces and updates possibleMoves
    public void calculateValidMoves(boolean currentPlayerIsWhite) {
        for (Square square : squares) {
            Piece piece = square.getPiece();
            
            if (piece == null || currentPlayerIsWhite != whiteToMove || piece.isWhite() != whiteToMove) {
                continue;
            }
            
            piece.calculatePossibleMoves(squares, square, currentMatch.getLastMove());
                
            // go through the piece's possible moves for validation
            for (Move move : piece.getPossibleMoves()) {
                // Tag this step as a simulation to protect tracking registries
                move.setSimulation(true); 

                // silently move piece by later reverting the move
                movePiece(move, false);

                // calculate opponents' moves after the move
                for (Square subsquare : squares) {
                    if (!subsquare.hasPiece() || subsquare.getPiece().isWhite() == whiteToMove) {
                        continue;
                    }
                    
                    subsquare.getPiece().calculatePossibleMoves(
                        squares, 
                        subsquare, 
                        move
                    );

                    boolean moveIsValid = true;

                    // go through the moves to see if any attack the king
                    for (Move submove : subsquare.getPiece().getPossibleMoves()) {
                        submove.isSimulation = true; // Mark nested checks as simulation as well
                        Square newSquare = submove.getNewSquare();
                        if (newSquare.hasPiece() && 
                            newSquare.getPiece().getName().equals("king") && 
                            newSquare.getPiece().isWhite() == whiteToMove) {
                            moveIsValid = false;
                            break;
                        }
                    }

                    if (!moveIsValid) {
                        move.setValid(false);
                        break;
                    }
                }

                // revert the move
                movePiece(move, true);
            }
        }
        
        // re-check if the king is under attack
        King kingToCheck = whiteToMove ? whiteKing : blackKing;
        kingToCheck.isAttacked = false;
        for (Square square : squares) {
            if (square.hasPiece() && square.getPiece().isWhite() != whiteToMove) {
                for (Move move : square.getPiece().getPossibleMoves()) {
                    if (move.isValid() && move.getNewSquare().getPiece() == kingToCheck) {
                        kingToCheck.isAttacked = true;
                        break;
                    }
                }
            }
        }
    }
    
    private void setStartingPosition() {
        for (int i = 0; i < squares.length; i++) {
            // every other square is black
            boolean isWhite = i % 2 == 0 && (i / 8) % 2 == 0 
                    || i % 2 != 0 && (i / 8) % 2 != 0;
            
            squares[i] = new Square(
                i, 
                startingPositionPiece(i), 
                isWhite
            );
        }
        
        // assign kings for easier future reference
        this.whiteKing = (King) squares[60].getPiece();
        this.blackKing = (King) squares[4].getPiece();
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
            return new King(i == 60);
        } 
        return null;
    }
    
    // checks if the index exists on the board (0-63 is valid)
    public static boolean indexIsValid(int index) {
        return index >= 0 && index < 64;
    }
    
}