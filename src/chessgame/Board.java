package chessgame;

import chessgame.pieces.Pawn;
import chessgame.pieces.Queen;
import chessgame.pieces.Rook;
import chessgame.pieces.King;
import chessgame.pieces.Bishop;
import chessgame.pieces.Knight;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arvid.renestam
 */
public final class Board {
    private final Square[] squares = new Square[65]; // extra square as default
    
    private King whiteKing;
    private King blackKing;
    
    private Match currentMatch;
    private boolean currentMatchHasStarted = false;
    
    private final int unselectedSquareIndex = 64;
    private int selectedSquareIndex = unselectedSquareIndex;
    
    private int boardSize;
    private int squareSize;
    
    private boolean whiteToMove;
    
    private Move lastMove;
    private Move secondLastMove;

    public Board() {
        whiteToMove = true;
        lastMove = new Move();
        secondLastMove = new Move();
        setStartingPosition();        
    }
    
    public int getBoardSize() {
        return boardSize;
    }
    
    public int getSquareSize() {
        return squareSize;
    }
    
    public Square[] getSquares() {
        return squares;
    }
    
    public boolean whiteToMove() {
        return whiteToMove;
    }
    
    public Move getLastMove() {
        return lastMove;
    }
    
    public Match getCurrentMatch() {
        return currentMatch;
    }
    
    // draw the squares in a grid
    void draw(Graphics g, ImageObserver observer) {
        // background color
        g.setColor(backgroundColor(true));
        g.fillRect(0, 0, Panel.width, Panel.height / 2);
        g.setColor(backgroundColor(false));
        g.fillRect(0, Panel.height / 2, Panel.width, Panel.height);
        
        // board size is the size of smallest panel dimension
        if (Panel.width - Leaderboard.WIDTH < Panel.height) {
            boardSize = Panel.width - Leaderboard.WIDTH - 2 * Panel.PADDING;
        } else {
            boardSize = Panel.height - 2 * Panel.PADDING;
        }
        
        // draw all squares
        squareSize = (int) Math.round(boardSize / 8.0);
        for (int i = 0; i < squares.length - 1; i++) {
            if (squares[i] != null) {
                squares[i].draw(g, observer, squareSize);
            }
        }
    }
    
    private Color backgroundColor(boolean isTop) {
        if (currentMatch == null) {
            return new Color(40, 25, 25); // default background
        }

        boolean noWinner = currentMatch.winner.isEmpty();
        boolean whiteWon = currentMatch.winner.equals("white");
        boolean blackWon = currentMatch.winner.equals("black");
        boolean stalemate = currentMatch.winner.equals("stalemate");

        if ((blackWon && isTop) || (whiteWon && !isTop)) {
            return new Color(40, 150, 25); // green for winner
        } else if ((blackWon && !isTop) || (whiteWon && isTop)) {
            return new Color(150, 25, 25); // red for loser
        } else if (stalemate) {
            return new Color(150, 150, 25); // yellow for stalemate
        } else if (noWinner && currentMatchHasStarted) {
            // highlight the current player to move
            if ((whiteToMove && !isTop) || (!whiteToMove && isTop)) {
                return new Color(50, 35, 35); // lighter highlight
            }
        }

        return new Color(40, 25, 25); // default background
    }
    
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
            if (square.hasPiece() && square.piece.isWhite() == whiteToMove) {
                boolean moveIsValid = false;
                for (Move move : square.piece.getPossibleMoves()) {
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
        setVisiblePossibleMoves();
        return true;
    }
    
    private void setStartingPosition() {
        for (int i = 0; i < squares.length; i++) {
            // every other square is black
            boolean isWhite = i % 2 == 0 && (i / 8) % 2 == 0 
                    || i % 2 != 0 && (i / 8) % 2 != 0;
            
            boolean isSelected = false;
            boolean isPossibleMove = false;
            
            squares[i] = new Square(
                i, 
                startingPositionPiece(i), 
                isWhite, 
                isSelected, 
                false,
                isPossibleMove
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
        return new Piece("", true);
    }
    
    // when a square is clicked - select square or move piece
    void selectSquare(int index) {
        if (!Board.indexIsValid(index)) return;
        
        Square oldSquare = new Square();
        if (selectedSquareIndex != unselectedSquareIndex) {
            oldSquare = squares[selectedSquareIndex];
        }
        Square newSquare = squares[index];
        
        // unselect square
        if (selectedSquareIndex == index) {
            newSquare.isSelected = false;
            selectedSquareIndex = unselectedSquareIndex;
            setVisiblePossibleMoves();
            return;
        }
        
        // move piece
        Move move = squares[selectedSquareIndex].piece.getPossibleMove(index);
        if (currentMatchHasStarted && isTurnOfSelectedPiece() && move != null && move.isValid) {
            movePiece(move, false);
            whiteToMove = !whiteToMove; // next player's turn

            // reset king's attacked status
            if (whiteToMove) {
                whiteKing.isAttacked = false;
            } else {
                blackKing.isAttacked = false;
            }
            
            if (!whiteToMove) currentMatch.incrementMovesPlayed();
            calculatePossibleMoves();
            
            if (!currentMatchHasStarted) currentMatchHasStarted = true;
            
            checkMatchIsWon();
        } 
        
        // select square
        else {
            selectedSquareIndex = index;
            oldSquare.isSelected = false;
            newSquare.isSelected = true;
            selectedSquareIndex = newSquare.getIndex();
        }
        
        if (currentMatchHasStarted) {
            setVisiblePossibleMoves();
        }
    }
    
    private void movePiece(Move move, boolean isRevert) {
        Square oldSquare = move.getOldSquare();
        Square newSquare = move.getNewSquare();
        
        Piece oldSquarePiece = move.getOldSquarePiece();
        Piece newSquarePiece = move.getNewSquarePiece();
        
        boolean isWhite = oldSquare.piece.isWhite();
        
        // remove the selections of last move, add new selections
        if (!isRevert) {
            lastMove.setIsLastMove(false);
            secondLastMove = lastMove;
            lastMove = new Move(oldSquare, newSquare);
            lastMove.setIsLastMove(true);            
        } else {
            lastMove.setIsLastMove(false);
            lastMove = secondLastMove;
            lastMove.setIsLastMove(true);
        }

        
        // update pieces to reflect the move
        if (!isRevert) {
            newSquare.piece = oldSquarePiece;
            oldSquare.removePiece();
        } else {
            oldSquare.piece = oldSquarePiece;
            newSquare.piece = newSquarePiece;
        }

        // promotion
        if (move.isPromotion) {
            if (!isRevert) {
                newSquare.piece = new Queen(isWhite);
            } else {
                newSquare.piece = move.getNewSquarePiece();
                oldSquare.piece = move.getOldSquarePiece();
            }
        }

        // en passant
        if (move.isEnPassant) {
            Square capturedPawnSquare = move.getAdditionalSquares()[0];
            if (!isRevert) {
                capturedPawnSquare.removePiece();
            } else {
                capturedPawnSquare.piece = move.getCapturedPiece();
            }
        }

        // castling
        if (move.isCastle) {
            Square oldRookSquare = move.getAdditionalSquares()[0];
            Square newRookSquare = move.getAdditionalSquares()[1];
            
            if (!isRevert) {
                newRookSquare.piece = oldRookSquare.piece;
                oldRookSquare.removePiece();
            } else {
                oldRookSquare.piece = newRookSquare.piece;
                newRookSquare.removePiece();
            }
        }

        if (!isRevert) {
            if (newSquare.piece.hasMoved) {
                newSquare.piece.hadMovedLastMove = true;
            } else {
                newSquare.piece.hasMoved = true;
            }
        } else {
            oldSquare.piece.hasMoved = oldSquare.piece.hadMovedLastMove;
            newSquare.piece.hasMoved = newSquare.piece.hadMovedLastMove;
        }
        
        selectedSquareIndex = unselectedSquareIndex;
    }


    // goes through all pieces and updates possibleMoves
    public void calculatePossibleMoves() {
        for (Square square : squares) {
            if (square.hasPiece() && square.piece.isWhite() == whiteToMove) {
                square.piece.calculatePossibleMoves(
                    squares, 
                    square, 
                    lastMove
                );
                
                // go through the piece's possible moves for validation
                for (Move move : square.piece.getPossibleMoves()) {
                    // silently move piece by later reverting the move
                    movePiece(move, false);
                    
                    // calculate opponents' moves after the move
                    for (Square subsquare : squares) {
                        if (subsquare.hasPiece() && subsquare.piece.isWhite() == !whiteToMove) {
                            subsquare.piece.calculatePossibleMoves(
                                squares, 
                                subsquare, 
                                move
                            );
                            
                            boolean moveIsValid = true;
                            
                            // go through the moves to see if any attack the king, which would the initial move invalid
                            for (Move submove : subsquare.piece.getPossibleMoves()) {
                                Square newSquare = submove.getNewSquare();
                                if (newSquare.piece.getName().equals("king") && 
                                        newSquare.piece.isWhite() == whiteToMove) {
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
            if (square.hasPiece() && square.piece.isWhite() != whiteToMove) {
                for (Move move : square.piece.getPossibleMoves()) {
                    if (move.isValid && move.getNewSquare().piece == kingToCheck) {
                        kingToCheck.isAttacked = true;
                        break;
                    }
                }
            }
        }
    }
    
    // updates each square with isPossibleMove parameter (small circle on square)
    private void setVisiblePossibleMoves() {
        // remove all marks
        for (Square square : squares) {
            square.isPossibleMove = false;
        }
        
        if (!isTurnOfSelectedPiece()) return;
        
        // add mark to selected piece
        if (selectedSquareIndex != unselectedSquareIndex && squares[selectedSquareIndex].hasPiece()) {
            for (Move move : squares[selectedSquareIndex].piece.getPossibleMoves()) {
                if (move.isValid) {
                    Square square = squares[move.getNewSquare().getIndex()];
                    square.isPossibleMove = true;
                }
            }
        }
    }
    
    private boolean isTurnOfSelectedPiece() {
        if (selectedSquareIndex == 65) return false;
        return squares[selectedSquareIndex].piece.isWhite() == whiteToMove;
    }
    
    // checks if the index exists on the board (0-63 is valid)
    public static boolean indexIsValid(int index) {
        return index >= 0 && index <= 63;
    }
    
}
