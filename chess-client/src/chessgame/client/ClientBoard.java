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
 * 
 * @author arvid.renestam
 */
public class ClientBoard {
    
    public static final Color DEFAULT_BG_COLOR = new Color(40, 25, 25);
    public static final Color LIGHTER_BG_COLOR = new Color(50, 35, 35);
    public static final Color VICTORY_BG_COLOR = new Color(40, 200, 25);
    public static final Color LOSS_BG_COLOR = new Color(150, 25, 25);
    public static final Color STALEMATE_BG_COLOR = new Color(150, 150, 25);
    
    Board board;
    private final Square[] squares; // references the board object's squares array
    
    private int boardSize;
    private int squareSize;
    
    private final int unselectedSquareIndex = 64;
    private int selectedSquareIndex = unselectedSquareIndex;
    
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
        
        if (currentMatch == null) { return DEFAULT_BG_COLOR; }
        
        String winner = currentMatch.getWinner();
       
        boolean noWinner = winner.isEmpty();
        boolean whiteWon = winner.equals("white");
        boolean blackWon = winner.equals("black");
        boolean stalemate = winner.equals("stalemate");

        if ((blackWon && isTop) || (whiteWon && !isTop)) {
            return VICTORY_BG_COLOR;
        } else if ((blackWon && !isTop) || (whiteWon && isTop)) {
            return LOSS_BG_COLOR;
        } else if (stalemate) {
            return STALEMATE_BG_COLOR;
        } else if (noWinner && board.currentMatchHasStarted()) {
            // highlight the current player to move
            if ((board.whiteToMove() && !isTop) || (!board.whiteToMove() && isTop)) {
                return LIGHTER_BG_COLOR;
            }
        }
        return DEFAULT_BG_COLOR;
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
        
        Match currentMatch = board.getCurrentMatch();
        Move lastMove = currentMatch == null ? null : currentMatch.getLastMove();
        int lastMoveOldIndex = lastMove != null && lastMove.getOldSquare() != null ? lastMove.getOldSquare().getIndex() : -1;
        int lastMoveNewIndex = lastMove != null && lastMove.getNewSquare() != null ? lastMove.getNewSquare().getIndex() : -1;
        for (int i = 0; i < 64; i++) {
            int actualIndex = isWhitePerspective ? i : (63 - i);
            
            if (squares[actualIndex] != null) {
                boolean isSelected = (actualIndex == selectedSquareIndex);
                boolean isPossibleMove = possibleMoves[actualIndex];
                boolean isLastMove = (actualIndex == lastMoveOldIndex || actualIndex == lastMoveNewIndex);
                
                Square visualSquare = new Square(i, squares[actualIndex].getPiece(), squares[actualIndex].isWhite());
                
                ClientSquare square = new ClientSquare(visualSquare, isSelected, isPossibleMove, isLastMove);
                square.draw(g, observer, squareSize, panelSize);
            }
        }
    }
    
    // when a square is clicked - select square or move piece
    void selectSquare(int targetIndex, ServerConnection serverConnection) {
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
            
            if (selectedPiece != null && selectedPiece.isWhite() == isWhitePerspective) {
                Move moveToMake = null;
                for (Move move : selectedPiece.getPossibleMoves()) {
                    if (move.getNewSquare().getIndex() == index) {
                        moveToMake = move;
                        break;
                    }
                }
                
                if (moveToMake != null) {
                    isValid = board.makeMove(moveToMake, isWhitePerspective);  
                    serverConnection.sendObject(moveToMake);
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
            if (selectedPiece.isWhite() == isWhitePerspective)
            for (Move move : selectedPiece.getPossibleMoves()) {
                if (move.isValid()) {
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