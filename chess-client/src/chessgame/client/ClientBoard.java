/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Board;
import chessgame.shared.Match;
import chessgame.shared.Move;
import chessgame.shared.Square;
import chessgame.shared.Piece; // Imported Piece
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 *
 * @author arvid
 */
public class ClientBoard {
    
    Board board;
    private final Square[] squares;
    
    private int boardSize;
    private int squareSize;
    
    private final int unselectedSquareIndex = 64;
    private int selectedSquareIndex = unselectedSquareIndex;
    
    // UI State replacing the shared Square fields
    private final boolean[] possibleMoves = new boolean[64];
    
    public ClientBoard(Board board) {
        this.board = board;
        this.squares = board.getSquares();
    }
    
    public Board getBoard() { return board; }
    public int getBoardSize() { return boardSize; }
    public int getSquareSize() { return squareSize; }
    
    private Color backgroundColor(boolean isTop) {
        Match currentMatch = board.getCurrentMatch();
        
        if (currentMatch == null) {
            return new Color(40, 25, 25); // default background
        }
        
        String winner = currentMatch.getWinner();
        
        boolean noWinner = winner.isEmpty();
        boolean whiteWon = winner.equals("white");
        boolean blackWon = winner.equals("black");
        boolean stalemate = winner.equals("stalemate");

        if ((blackWon && isTop) || (whiteWon && !isTop)) {
            return new Color(40, 150, 25); // green for winner
        } else if ((blackWon && !isTop) || (whiteWon && isTop)) {
            return new Color(150, 25, 25); // red for loser
        } else if (stalemate) {
            return new Color(150, 150, 25); // yellow for stalemate
        } else if (noWinner && board.currentMatchHasStarted()) {
            // highlight the current player to move
            if ((board.whiteToMove() && !isTop) || (!board.whiteToMove() && isTop)) {
                return new Color(50, 35, 35); // lighter highlight
            }
        }

        return new Color(40, 25, 25); // default background
    }
    
    // draw the squares in a grid
    void draw(Graphics g, ImageObserver observer, Dimension panelSize) {
        int width = panelSize.width;
        int height = panelSize.height;
        int padding = GamePanel.PADDING;
        
        // background color
        g.setColor(backgroundColor(true));
        g.fillRect(0, 0, width, height / 2);
        g.setColor(backgroundColor(false));
        g.fillRect(0, height / 2, width, height);
        
        // board size is the size of smallest panel dimension
        if (width - Leaderboard.WIDTH < height) {
            boardSize = width - Leaderboard.WIDTH - 2 * padding;
        } else {
            boardSize = height - 2 * padding;
        }
        
        // draw all squares
        squareSize = (int) Math.round(boardSize / 8.0);
        Square[] squares = board.getSquares();
        
        // Fetch the last move to determine highlight colors
        Move lastMove = board.getLastMove();
        int lastMoveOldIndex = lastMove != null && lastMove.getOldSquare() != null ? lastMove.getOldSquare().getIndex() : -1;
        int lastMoveNewIndex = lastMove != null && lastMove.getNewSquare() != null ? lastMove.getNewSquare().getIndex() : -1;
        
        // FIXED: Loop over all 64 squares (removed the - 1)
        for (int i = 0; i < squares.length; i++) {
            if (squares[i] != null) {
                // Determine UI state for the square
                boolean isSelected = (i == selectedSquareIndex);
                boolean isPossibleMove = possibleMoves[i];
                boolean isLastMove = (i == lastMoveOldIndex || i == lastMoveNewIndex);
                
                // Pass the UI states directly into the ClientSquare
                ClientSquare square = new ClientSquare(squares[i], isSelected, isPossibleMove, isLastMove);
                square.draw(g, observer, squareSize, panelSize);
            }
        }
    }
    
    // when a square is clicked - select square or move piece
    void selectSquare(int index) {
        if (!Board.indexIsValid(index)) return;
        
        // unselect square
        if (selectedSquareIndex == index) {
            selectedSquareIndex = unselectedSquareIndex;
            setVisiblePossibleMoves();
            return;
        }
        
        boolean whiteToMove = board.whiteToMove();
        boolean isValid = false;
        
        // move piece (FIXED: Null-safety and encapsulated move lookup)
        if (selectedSquareIndex != unselectedSquareIndex && isTurnOfSelectedPiece()) {
            Piece selectedPiece = squares[selectedSquareIndex].getPiece();
            
            if (selectedPiece != null) {
                Move moveToMake = null;
                // Find the specific move in the piece's possible moves
                for (Move move : selectedPiece.getPossibleMoves()) {
                    if (move.getNewSquare().getIndex() == index) {
                        moveToMake = move;
                        break;
                    }
                }
                
                if (moveToMake != null) {
                    isValid = board.movePiece(moveToMake);  
                }
            }
        } 
        
        if (isValid) {
            // Note: board.movePiece() already flips whiteToMove, so we check the new turn state
            if (board.whiteToMove()) {
                board.getWhiteKing().isAttacked = false;
            } else {
                board.getBlackKing().isAttacked = false;
            }
            
            board.checkMatchIsWon();
            
            // Deselect piece after moving
            selectedSquareIndex = unselectedSquareIndex;
        }
        // select square
        else {
            selectedSquareIndex = index;
        }
        
        if (board.currentMatchHasStarted()) {
            setVisiblePossibleMoves();
        }
    }
    
    // updates the possibleMoves array for UI rendering
    private void setVisiblePossibleMoves() {
        // remove all marks
        for (int i = 0; i < possibleMoves.length; i++) {
            possibleMoves[i] = false;
        }
        
        if (!isTurnOfSelectedPiece()) return;
        
        // add mark to valid moves of the selected piece
        if (selectedSquareIndex != unselectedSquareIndex && squares[selectedSquareIndex].hasPiece()) {
            Piece selectedPiece = squares[selectedSquareIndex].getPiece();
            for (Move move : selectedPiece.getPossibleMoves()) {
                if (move.isValid) {
                    possibleMoves[move.getNewSquare().getIndex()] = true;
                }
            }
        }
    }
    
    private boolean isTurnOfSelectedPiece() {
        // FIXED: Used unselectedSquareIndex instead of hardcoded 65, and added null check
        if (selectedSquareIndex == unselectedSquareIndex) return false;
        
        Piece piece = board.getSquares()[selectedSquareIndex].getPiece();
        if (piece == null) return false;
        
        return piece.isWhite() == board.whiteToMove();
    }
    
}
