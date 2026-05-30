/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Board;
import chessgame.shared.Leaderboard;
import chessgame.shared.Match;
import chessgame.shared.Move;
import chessgame.shared.Player;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * @author arvid.renestam
 */
public class GamePanel extends javax.swing.JPanel {
    
    public static final ClientBoard board = new ClientBoard(new Board());
    public static final ClientLeaderboard leaderboard = new ClientLeaderboard(new Leaderboard());
    
    private ServerConnection serverConnection;
    
    public static final int PADDING = 30;
    
    public GamePanel() {
        initComponents();
        updateLeaderboard();
    }
    
    public static ClientBoard getClientBoard() {
        return board;
    }
    
    public void setServerConnection(ServerConnection connection) {
        this.serverConnection = connection;
    }
    
    public String getCurrentPlayerName() {
        return txfCurrentPlayer.getText().trim();
    }
    
    public void startMatch(Match match) {
        // Determine who our opponent is based on who we typed in
        String myName = getCurrentPlayerName();
        String opponentName = match.getWhitePlayer().getName().equalsIgnoreCase(myName) 
                ? match.getBlackPlayer().getName() 
                : match.getWhitePlayer().getName();

        java.awt.EventQueue.invokeLater(() -> {
            txfOpponentPlayer.setText(opponentName);
            btnStopSearch.setEnabled(false);
        });
        
        board.setIsWhitePerspective(match.getCurrentPlayerIsWhite());
        board.getBoard().startNewMatch(match, board.getIsWhitePerspective());
        
        repaint();
    }
    
    public void endMatch(Match match) {
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.draw(g, this, getSize());
    }
    
    public int getPanelWidth() { return getSize().width; } 
    public int getPanelHeight() { return getSize().height; }
    
    private void updateLeaderboard() {
        txaLeaderboard.setText(leaderboard.getLeaderboardText());
        txaGames.setText(leaderboard.getMatchesText());
    }
    
    public void setIsWhitePerspective(boolean isWhitePerspective) {
        board.setIsWhitePerspective(isWhitePerspective);
    }

    public void handleIncomingMove(Move move) {
        java.awt.EventQueue.invokeLater(() -> {
            if (!board.getBoard().makeMove(move, board.getBoard().whiteToMove())) {
                System.out.println("Unable to make incoming move.");
            } else {
                System.out.println("Successfuly made incoming move.");
            }
            repaint();
        });
    }
    
    // select a square
    private void formMousePressed(java.awt.event.MouseEvent evt) {                                                                          
        int posX = evt.getX();
        int posY = evt.getY();
        
        int squareSize = board.getSquareSize();
        int boardSize = board.getBoardSize();
        
        int boardX = PADDING;
        int boardY = (getPanelHeight() - boardSize) / 2;
        
        // check if click was outside board
        if (posX < boardX || posY < boardY || posX > boardX + boardSize || posY > boardY + boardSize) {
            return;
        }

        // calculate which square was clicked
        int xIndex = (posX - boardX) / squareSize;
        int yIndex = (posY - boardY) / squareSize;
        int squareIndex = yIndex * 8 + xIndex;
        
        // update the clicked square
        board.selectSquare(squareIndex, serverConnection);
        repaint();
    }
    
    // start looking for a game
    private void startGame(java.awt.event.ActionEvent evt) {                                             
        String whitePlayerName = txfCurrentPlayer.getText().trim();
        String ipAddress = txfServerAddress.getText().trim();
        
        if (whitePlayerName.isBlank()) return;
        if (ipAddress.isBlank()) {
            ipAddress = "localhost";
        }
        
        txfCurrentPlayer.setEditable(false);
        txfCurrentPlayer.setFocusable(false);
        txfServerAddress.setEditable(false);
        txfServerAddress.setFocusable(false);
        
        btnStartGame.setEnabled(false);
        btnStopSearch.setEnabled(true);
        
        Player localPlayer = leaderboard.getLeaderboard().getPlayer(whitePlayerName);
        
        if (this.serverConnection == null) {
            this.serverConnection = new ServerConnection(ipAddress, 3000, this);
        }
        
        this.serverConnection.sendObject(localPlayer);
        repaint();
    }                                             

    // cancel looking for a game
    private void stopSearch(java.awt.event.ActionEvent evt) {
        if (this.serverConnection != null) {
            this.serverConnection.stop();
            this.serverConnection = null;
        }
        
        txfCurrentPlayer.setEditable(true);
        txfCurrentPlayer.setFocusable(true);
        txfServerAddress.setEditable(true);
        txfServerAddress.setFocusable(true);
        
        btnStartGame.setEnabled(true);
        btnStopSearch.setEnabled(false);
        
        System.out.println("Matchmaking search canceled by user.");
        repaint();
    }
              
    private JButton btnStartGame;
    private JButton btnStopSearch;
    private JLabel lblCurrentPlayer;
    private JLabel lblOpponentPlayer;
    private JLabel lblServerAddress;
    private JLabel lblLeaderboard;
    private JLabel lblGames;
    private JPanel sidePanel;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private JTextArea txaGames;
    private JTextArea txaLeaderboard;
    private JTextField txfOpponentPlayer;
    private JTextField txfCurrentPlayer;
    private JTextField txfServerAddress;
    
    private void initComponents() {
        sidePanel = new javax.swing.JPanel();
        
        txfCurrentPlayer = new javax.swing.JTextField();
        txfCurrentPlayer.setDisabledTextColor(Color.BLACK);
        txfOpponentPlayer = new javax.swing.JTextField();
        txfOpponentPlayer.setEnabled(false);
        txfOpponentPlayer.setDisabledTextColor(Color.BLACK);
        
        txfServerAddress = new javax.swing.JTextField("localhost");
        
        btnStartGame = new javax.swing.JButton();
        btnStartGame.setEnabled(false);
        
        btnStopSearch = new javax.swing.JButton();
        btnStopSearch.setEnabled(false);
        
        lblCurrentPlayer = new javax.swing.JLabel("ENTER YOUR NAME:");
        lblOpponentPlayer = new javax.swing.JLabel("OPPONENT");
        lblServerAddress = new javax.swing.JLabel("SERVER IP ADDRESS:");
        lblLeaderboard = new javax.swing.JLabel("LEADERBOARD");
        lblGames = new javax.swing.JLabel("GAMES");
        
        jScrollPane1 = new javax.swing.JScrollPane();
        txaGames = new javax.swing.JTextArea(5, 15);
        jScrollPane3 = new javax.swing.JScrollPane();
        txaLeaderboard = new javax.swing.JTextArea(5, 20);

        txfCurrentPlayer.getDocument().addDocumentListener(new DocumentListener() {
            private void updateLabel() {
                if (txfCurrentPlayer.getText().trim().isEmpty()) {
                    lblCurrentPlayer.setText("ENTER YOUR NAME");
                    btnStartGame.setEnabled(false);
                } else {
                    lblCurrentPlayer.setText("YOU");
                    if (serverConnection == null) {
                        btnStartGame.setEnabled(true);
                    }
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { updateLabel(); }
            @Override public void removeUpdate(DocumentEvent e) { updateLabel(); }
            @Override public void changedUpdate(DocumentEvent e) { updateLabel(); }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        sidePanel.setBackground(new java.awt.Color(189, 112, 64)); 
        sidePanel.setEnabled(false);
        sidePanel.setFocusable(false);
        sidePanel.setPreferredSize(new java.awt.Dimension(300, 539));
        sidePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        btnStartGame.setText("LOOK FOR GAME");
        btnStartGame.addActionListener(this::startGame);
        
        btnStopSearch.setText("STOP SEARCH");
        btnStopSearch.addActionListener(this::stopSearch);

        txaGames.setEditable(false);
        txaGames.setBackground(java.awt.Color.WHITE);
        txaGames.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txaGames.setFocusable(false);
        jScrollPane1.setViewportView(txaGames);

        txaLeaderboard.setEditable(false);
        txaLeaderboard.setBackground(java.awt.Color.WHITE);
        txaLeaderboard.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txaLeaderboard.setText("1. \n2. \n3.\n4.\n5.");
        txaLeaderboard.setFocusable(false);
        jScrollPane3.setViewportView(txaLeaderboard);

        // Sidebar layout configuration
        sidePanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(4, 0, 4, 0); 
        gbc.weightx = 1.0;

        // Row 0: Player Labels
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.5; sidePanel.add(lblCurrentPlayer, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; gbc.insets.left = 10; sidePanel.add(lblOpponentPlayer, gbc);
        gbc.insets.left = 0; 

        // Row 1: Player Input Fields
        gbc.gridy = 1;
        gbc.gridx = 0; sidePanel.add(txfCurrentPlayer, gbc);
        gbc.gridx = 1; gbc.insets.left = 10; sidePanel.add(txfOpponentPlayer, gbc);
        gbc.insets.left = 0; gbc.weightx = 1.0; gbc.gridwidth = 2; 

        // Row 2: Server Address Label
        gbc.gridy = 2; gbc.gridx = 0; gbc.insets.top = 8; sidePanel.add(lblServerAddress, gbc); gbc.insets.top = 4;

        // Row 3: Server Address Field
        gbc.gridy = 3; gbc.gridx = 0; sidePanel.add(txfServerAddress, gbc);

        // Row 4: Match Action Buttons (Side-by-side split cells)
        gbc.gridy = 4; gbc.insets.top = 8;
        gbc.gridwidth = 1; 
        
        gbc.gridx = 0; gbc.weightx = 0.5; 
        sidePanel.add(btnStartGame, gbc); 
        
        gbc.gridx = 1; gbc.weightx = 0.5; gbc.insets.left = 10; 
        sidePanel.add(btnStopSearch, gbc);
        
        // ─── THE CRITICAL RESET FIX ───
        // Reset gridx back to 0, clear horizontal left padding, expand width back to double columns
        gbc.gridx = 0; 
        gbc.insets.left = 0; 
        gbc.insets.top = 4; 
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0;

        // Row 5 & 6: Leaderboard Section
        gbc.gridy = 5; gbc.insets.top = 12; sidePanel.add(lblLeaderboard, gbc); gbc.insets.top = 4;
        gbc.gridy = 6; sidePanel.add(jScrollPane3, gbc);

        // Row 7 & 8: Matches History Section
        gbc.gridy = 7; gbc.insets.top = 12; sidePanel.add(lblGames, gbc); gbc.insets.top = 4;
        gbc.gridy = 8; 
        gbc.weighty = 1.0; 
        gbc.fill = java.awt.GridBagConstraints.BOTH; 
        sidePanel.add(jScrollPane1, gbc);

        // Main layout integration
        this.setLayout(new java.awt.BorderLayout());
        this.add(sidePanel, java.awt.BorderLayout.EAST);
    }
}