/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Board;
import chessgame.shared.Match;
import chessgame.shared.Move;
import chessgame.shared.Square;
import chessgame.shared.Piece; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 * Handles the client-side board view rendering and flipped index perspective configurations.
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
    
    private boolean isWhitePerspective = true; 
    
    public ClientBoard(Board board) {
        this.board = board;
        this.squares = board.getSquares();
    }
    
    public Board getBoard() { return board; }
    public int getBoardSize() { return boardSize; }
    public int getSquareSize() { return squareSize; }
    
    public void setIsWhitePerspective(boolean isWhite) {
        this.isWhitePerspective = isWhite;
    }
    
    public boolean getIsWhitePerspective() {
        return this.isWhitePerspective;
    }
    
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
        if (width - ClientLeaderboard.WIDTH < height) {
            boardSize = width - ClientLeaderboard.WIDTH - 2 * padding;
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
        
        // Loop through visual grid spaces
        for (int i = 0; i < 64; i++) {
            // Invert index logic for matching the correct state array cell
            int actualIndex = isWhitePerspective ? i : (63 - i);
            
            if (squares[actualIndex] != null) {
                // Determine UI state using the actual raw array data index
                boolean isSelected = (actualIndex == selectedSquareIndex);
                boolean isPossibleMove = possibleMoves[actualIndex];
                boolean isLastMove = (actualIndex == lastMoveOldIndex || actualIndex == lastMoveNewIndex);
                
                // Construct a temporary placeholder visual square matching your specific constructor.
                // It maps the current screen draw index 'i', loads the piece data, and replicates 
                // the base color identity layout properties safely.
                Square visualSquare = new Square(i, squares[actualIndex].getPiece(), squares[actualIndex].isWhite());
                
                ClientSquare square = new ClientSquare(visualSquare, isSelected, isPossibleMove, isLastMove);
                square.draw(g, observer, squareSize, panelSize);
            }
        }
    }
    
    // when a square is clicked - select square or move piece
    void selectSquare(int targetIndex) {
        // Invert index logic for transforming screen coordinates to engine coordinates
        int index = isWhitePerspective ? targetIndex : (63 - targetIndex);

        if (!Board.indexIsValid(index)) return;
        
        // unselect square
        if (selectedSquareIndex == index) {
            selectedSquareIndex = unselectedSquareIndex;
            setVisiblePossibleMoves();
            return;
        }
        
        boolean isValid = false;
        
        // move piece
        if (selectedSquareIndex != unselectedSquareIndex && isTurnOfSelectedPiece()) {
            Piece selectedPiece = squares[selectedSquareIndex].getPiece();
            
            if (selectedPiece != null) {
                Move moveToMake = null;
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
            if (board.whiteToMove()) {
                board.getWhiteKing().isAttacked = false;
            } else {
                board.getBlackKing().isAttacked = false;
            }
            
            board.checkMatchIsWon();
            selectedSquareIndex = unselectedSquareIndex;
        }
        else {
            selectedSquareIndex = index;
        }
        
        if (board.currentMatchHasStarted()) {
            setVisiblePossibleMoves();
        }
    }
    
    // updates the possibleMoves array for UI rendering
    private void setVisiblePossibleMoves() {
        for (int i = 0; i < possibleMoves.length; i++) {
            possibleMoves[i] = false;
        }
        
        if (!isTurnOfSelectedPiece()) return;
        
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
        if (selectedSquareIndex == unselectedSquareIndex) return false;
        
        Piece piece = board.getSquares()[selectedSquareIndex].getPiece();
        if (piece == null) return false;
        
        return piece.isWhite() == board.whiteToMove();
    }
}