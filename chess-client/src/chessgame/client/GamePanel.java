/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Board;
import chessgame.shared.Match;
import chessgame.shared.Player;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author arvid.renestam
 */
public class GamePanel extends javax.swing.JPanel {
    
    public static final ClientBoard board = new ClientBoard(new Board());
    public static final Leaderboard leaderboard = new Leaderboard();
    
    public static final int PADDING = 30;
    
    public GamePanel() {
        initComponents();
        updateLeaderboard();
    }
    
    public static ClientBoard getBoard() {
        return board;
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
        board.selectSquare(squareIndex);
        repaint();
    }
    
    // start new game
    private void btnStartGameActionPerformed(java.awt.event.ActionEvent evt) {                                             
        String whitePlayerName = txfWhitePlayer.getText().trim();
        String blackPlayerName = txfBlackPlayer.getText().trim();
        
        if (whitePlayerName.isBlank() || blackPlayerName.isBlank()) return;
        if (whitePlayerName.toLowerCase().equals(blackPlayerName.toLowerCase())) return;
        
        // make fields non-editable
        txfWhitePlayer.setEditable(false);
        txfWhitePlayer.setFocusable(false);
        txfBlackPlayer.setEditable(false);
        txfBlackPlayer.setFocusable(false);
        
        // change enabled button
        btnStartGame.setEnabled(false);
        btnEndGame.setEnabled(true);
        
        Player whitePlayer = leaderboard.getPlayer(whitePlayerName);
        Player blackPlayer = leaderboard.getPlayer(blackPlayerName);
        
        Match match = new Match(whitePlayer, blackPlayer);
        
        board.getBoard().startNewMatch(match);
        repaint();
    }                                            

    private void btnEndGameActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // add the match to leaderboard
        Match currentMatch = board.getBoard().getCurrentMatch();
        if (currentMatch != null) {
            boolean matchIsWon = board.getBoard().checkMatchIsWon();
            if (!matchIsWon) return;
            leaderboard.addMatch(currentMatch, true);
        }
        
        // change enabled button
        btnStartGame.setEnabled(true);
        btnEndGame.setEnabled(false);
        
        // make fields editable
        txfWhitePlayer.setEditable(true);
        txfWhitePlayer.setFocusable(true);
        txfBlackPlayer.setEditable(true);
        txfBlackPlayer.setFocusable(true);
        
        // clear fields
        txfWhitePlayer.setText("");
        txfBlackPlayer.setText("");
        
        updateLeaderboard();
        repaint();
    }
              
    private JButton btnEndGame;
    private JButton btnStartGame;
    private JLabel lblWhitePlayer;
    private JLabel lblBlackPlayer;
    private JLabel lblLeaderboard;
    private JLabel lblGames;
    private JPanel sidePanel;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private JTextArea txaGames;
    private JTextArea txaLeaderboard;
    private JTextField txfBlackPlayer;
    private JTextField txfWhitePlayer;
    
    private void initComponents() {
        sidePanel = new javax.swing.JPanel();
        txfWhitePlayer = new javax.swing.JTextField();
        txfBlackPlayer = new javax.swing.JTextField();
        btnStartGame = new javax.swing.JButton();
        btnEndGame = new javax.swing.JButton();
        lblWhitePlayer = new javax.swing.JLabel("WHITE PLAYER");
        lblBlackPlayer = new javax.swing.JLabel("BLACK PLAYER");
        lblLeaderboard = new javax.swing.JLabel("LEADERBOARD");
        lblGames = new javax.swing.JLabel("GAMES");
        
        jScrollPane1 = new javax.swing.JScrollPane();
        txaGames = new javax.swing.JTextArea(5, 15);
        jScrollPane3 = new javax.swing.JScrollPane();
        txaLeaderboard = new javax.swing.JTextArea(5, 20);

        // Setup Main Panel Click Listener
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        // Setup Component Properties
        sidePanel.setBackground(new java.awt.Color(189, 112, 64)); // Consider updating this color later!
        sidePanel.setEnabled(false);
        sidePanel.setFocusable(false);
        sidePanel.setPreferredSize(new java.awt.Dimension(300, 539));
        sidePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        btnStartGame.setText("START GAME");
        btnStartGame.addActionListener(this::btnStartGameActionPerformed);

        btnEndGame.setText("END GAME");
        btnEndGame.setEnabled(false);
        btnEndGame.addActionListener(this::btnEndGameActionPerformed);

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

        // Sidebar layout
        sidePanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(4, 0, 4, 0); // Spacing between rows
        gbc.weightx = 1.0;

        // Row 0: Player Labels
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.5; sidePanel.add(lblWhitePlayer, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; gbc.insets.left = 10; sidePanel.add(lblBlackPlayer, gbc);
        gbc.insets.left = 0; // Reset left margin

        // Row 1: Player Input Fields
        gbc.gridy = 1;
        gbc.gridx = 0; sidePanel.add(txfWhitePlayer, gbc);
        gbc.gridx = 1; gbc.insets.left = 10; sidePanel.add(txfBlackPlayer, gbc);
        gbc.insets.left = 0; gbc.weightx = 1.0; gbc.gridwidth = 2; // Merge columns back

        // Row 2 & 3: Match Buttons
        gbc.gridy = 2; gbc.gridx = 0; sidePanel.add(btnStartGame, gbc);
        gbc.gridy = 3; sidePanel.add(btnEndGame, gbc);

        // Row 4 & 5: Leaderboard Section
        gbc.gridy = 4; gbc.insets.top = 12; sidePanel.add(lblLeaderboard, gbc); gbc.insets.top = 4;
        gbc.gridy = 5; sidePanel.add(jScrollPane3, gbc);

        // Row 6 & 7: Matches History Section (Expands vertically to fill space)
        gbc.gridy = 6; gbc.insets.top = 12; sidePanel.add(lblGames, gbc); gbc.insets.top = 4;
        gbc.gridy = 7; 
        gbc.weighty = 1.0; 
        gbc.fill = java.awt.GridBagConstraints.BOTH; 
        sidePanel.add(jScrollPane1, gbc);

        // Main layout: Chess board on Left, Sidebar on Right
        this.setLayout(new java.awt.BorderLayout());
        this.add(sidePanel, java.awt.BorderLayout.EAST);
    }
    
}
